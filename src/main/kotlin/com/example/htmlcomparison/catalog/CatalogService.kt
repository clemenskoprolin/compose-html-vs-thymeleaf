package com.example.htmlcomparison.catalog

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Service
class CatalogService(
    private val klibsGateway: KlibsGateway,
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val cache = ConcurrentHashMap<String, CachedSearch>()

    fun page(rawQuery: String?): CatalogPage {
        val query = rawQuery.orEmpty().trim().take(MAX_QUERY_LENGTH)
        if (query.isBlank()) {
            return CatalogPage(
                query = "",
                projects = emptyList(),
                status = "Top repositories in each category, ranked by GitHub stars.",
                categories = FeaturedCatalogSections,
            )
        }

        val cached = cache[query]
            ?.takeIf { Duration.between(it.createdAt, Instant.now()) < CACHE_TTL }
            ?.projects

        return try {
            val projects = cached ?: klibsGateway.searchProjects(query).also {
                cache[query] = CachedSearch(Instant.now(), it)
            }
            CatalogPage(
                query = query,
                projects = projects,
                status = when (projects.size) {
                    0 -> "No projects found for \u201c$query\u201d."
                    1 -> "1 project found for \u201c$query\u201d via the klibs.io MCP server."
                    else -> "${projects.size} projects found for \u201c$query\u201d via the klibs.io MCP server."
                },
            )
        } catch (exception: Exception) {
            logger.warn("klibs.io MCP search failed for query '{}'", query, exception)
            CatalogPage(
                query = query,
                projects = SearchFallbackProjects,
                status = "Showing the local preview instead.",
                warning = "The live klibs.io MCP search is temporarily unavailable: ${exception.userMessage()}",
            )
        }
    }

    private fun Exception.userMessage(): String = message
        ?.lineSequence()
        ?.firstOrNull()
        ?.take(180)
        ?: javaClass.simpleName

    private data class CachedSearch(
        val createdAt: Instant,
        val projects: List<ProjectCard>,
    )

    companion object {
        private const val MAX_QUERY_LENGTH = 120
        private val CACHE_TTL: Duration = Duration.ofMinutes(2)

        private val SearchFallbackProjects = listOf(
            ProjectCard(
                name = "Compose Multiplatform",
                author = "JetBrains",
                description = "Share declarative user interfaces across Android, iOS, desktop, and web from Kotlin.",
                url = "https://klibs.io/project/JetBrains/compose-multiplatform",
                platforms = listOf("Android", "iOS", "JVM", "JS", "Wasm"),
                packages = listOf(
                    ProjectPackage(
                        groupId = "org.jetbrains.compose.runtime",
                        artifactId = "runtime",
                        latestVersion = "1.10.1",
                        latestStableVersion = "1.10.1",
                    ),
                ),
                totalPackages = 100,
            ),
            ProjectCard(
                name = "Ksoup",
                author = "MohamedRejeb",
                description = "A lightweight Kotlin Multiplatform library for parsing HTML and decoding HTML entities.",
                url = "https://klibs.io/project/MohamedRejeb/Ksoup",
                platforms = listOf("Common", "JS", "JVM", "Native", "Wasm"),
                packages = listOf(
                    ProjectPackage(
                        groupId = "com.mohamedrejeb.ksoup",
                        artifactId = "ksoup-html",
                        latestVersion = "0.6.0",
                        latestStableVersion = null,
                    ),
                ),
                totalPackages = 3,
            ),
            ProjectCard(
                name = "kotlinx.html",
                author = "Kotlin",
                description = "A type-safe Kotlin DSL for producing HTML with JVM, JavaScript, Native, and Wasm targets.",
                url = "https://klibs.io/project/Kotlin/kotlinx.html",
                platforms = listOf("Common", "JS", "JVM", "Native", "Wasm"),
                packages = listOf(
                    ProjectPackage(
                        groupId = "org.jetbrains.kotlinx",
                        artifactId = "kotlinx-html",
                        latestVersion = "0.12.0",
                        latestStableVersion = null,
                    ),
                ),
                totalPackages = 1,
            ),
        )
    }
}

package com.example.htmlcomparison.catalog

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Service
class CatalogService(
    private val klibsGateway: KlibsGateway,
    private val readmeGateway: ReadmeGateway,
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val cache = ConcurrentHashMap<SearchKey, CachedSearch>()
    private val projectCache = ConcurrentHashMap<ProjectKey, CachedProject>()
    private val readmeCache = ConcurrentHashMap<ProjectKey, CachedReadme>()

    fun page(
        rawQuery: String?,
        rawPlatforms: Collection<String>? = null,
    ): CatalogPage {
        val query = rawQuery.orEmpty().trim().take(MAX_QUERY_LENGTH)
        val platforms = Platform.select(rawPlatforms)
        if (query.isBlank()) {
            val categories = FeaturedCatalogSections.supporting(platforms)
            return CatalogPage(
                query = "",
                platforms = platforms,
                topTags = TopTags,
                projects = emptyList(),
                status = "Top repositories in each category, ranked by GitHub stars.",
                categories = categories,
                warning = "No featured project targets ${platforms.labels()}."
                    .takeIf { platforms.isNotEmpty() && categories.isEmpty() },
            )
        }

        val key = SearchKey(query, platforms)
        val cached = cache[key]
            ?.takeIf { Duration.between(it.createdAt, Instant.now()) < CACHE_TTL }
            ?.projects

        return try {
            val projects = cached ?: klibsGateway.searchProjects(query, platforms).also {
                cache[key] = CachedSearch(Instant.now(), it)
            }
            CatalogPage(
                query = query,
                platforms = platforms,
                topTags = TopTags,
                projects = projects,
                status = status(projects.size, query, platforms),
            )
        } catch (exception: Exception) {
            logger.warn("klibs.io MCP search failed for query '{}'", query, exception)
            CatalogPage(
                query = query,
                platforms = platforms,
                topTags = TopTags,
                projects = SearchFallbackProjects.supporting(platforms),
                status = "Showing the local preview instead.",
                warning = "The live klibs.io MCP search is temporarily unavailable: ${exception.userMessage()}",
            )
        }
    }

    fun project(
        rawAuthor: String?,
        rawName: String?,
        rawTab: String? = null,
    ): ProjectPage {
        val author = rawAuthor.orEmpty().trim().take(MAX_QUERY_LENGTH)
        val name = rawName.orEmpty().trim().take(MAX_QUERY_LENGTH)
        val tab = ProjectTab.of(rawTab)
        if (author.isBlank() || name.isBlank()) {
            return ProjectPage(author, name, tab = tab, warning = "That project link is incomplete.")
        }

        val project = try {
            lookup(author, name)
        } catch (exception: Exception) {
            logger.warn("klibs.io MCP lookup failed for '{}/{}'", author, name, exception)
            return ProjectPage(
                author = author,
                name = name,
                tab = tab,
                warning = "The live klibs.io MCP lookup is temporarily unavailable: ${exception.userMessage()}",
            )
        }

        if (project == null) {
            return ProjectPage(
                author = author,
                name = name,
                tab = tab,
                warning = "klibs.io has no project called \u201c$author/$name\u201d.",
            )
        }

        // The packages tab needs no README, so the extra request only happens when it shows.
        val readme = if (tab == ProjectTab.README) readme(author, name) else null
        return ProjectPage(
            author = author,
            name = name,
            project = project,
            readme = readme,
            readmeNotice = readmeNotice(readme, tab),
            tab = tab,
        )
    }

    private fun lookup(author: String, name: String): ProjectCard? {
        val key = ProjectKey(author, name)
        projectCache[key]
            ?.takeIf { Duration.between(it.createdAt, Instant.now()) < CACHE_TTL }
            ?.let { return it.project }

        return klibsGateway.findProject(author, name)
            .also { projectCache[key] = CachedProject(Instant.now(), it) }
    }

    private fun readme(author: String, name: String): ProjectReadme? {
        val key = ProjectKey(author, name)
        readmeCache[key]
            ?.takeIf { Duration.between(it.createdAt, Instant.now()) < CACHE_TTL }
            ?.let { return it.readme }

        return try {
            readmeGateway.readme(author, name)
                .also { readmeCache[key] = CachedReadme(Instant.now(), it) }
        } catch (exception: Exception) {
            logger.warn("README lookup failed for '{}/{}'", author, name, exception)
            null
        }
    }

    private fun readmeNotice(readme: ProjectReadme?, tab: ProjectTab): String? = when {
        tab != ProjectTab.README || readme != null -> null
        else -> "No README could be read from the project's GitHub repository."
    }

    private fun status(
        matches: Int,
        query: String,
        platforms: List<String>,
    ): String {
        val scope = if (platforms.isEmpty()) "" else " on ${platforms.labels()}"
        return when (matches) {
            0 -> "No projects found for “$query”$scope."
            1 -> "1 project found for “$query”$scope via the klibs.io MCP server."
            else -> "$matches projects found for “$query”$scope via the klibs.io MCP server."
        }
    }

    private fun List<String>.labels(): String = joinToString(", ", transform = Platform::labelOf)

    /** Mirrors the MCP filter: a project has to support every selected platform. */
    private fun List<ProjectCard>.supporting(platforms: List<String>): List<ProjectCard> {
        if (platforms.isEmpty()) return this
        val labels = platforms.map(Platform::labelOf)
        return filter { project -> project.displayedPlatforms.containsAll(labels) }
    }

    @JvmName("categoriesSupporting")
    private fun List<ProjectCategory>.supporting(platforms: List<String>): List<ProjectCategory> {
        if (platforms.isEmpty()) return this
        val labels = platforms.map(Platform::labelOf)
        return mapNotNull { category ->
            category.projects
                .filter { project -> project.platforms.containsAll(labels) }
                .takeIf(List<RankedProject>::isNotEmpty)
                ?.let { category.copy(projects = it) }
        }
    }

    private fun Exception.userMessage(): String = message
        ?.lineSequence()
        ?.firstOrNull()
        ?.take(180)
        ?: javaClass.simpleName

    private data class ProjectKey(
        val author: String,
        val name: String,
    )

    private data class CachedReadme(
        val createdAt: Instant,
        val readme: ProjectReadme?,
    )

    private data class CachedProject(
        val createdAt: Instant,
        val project: ProjectCard?,
    )

    private data class SearchKey(
        val query: String,
        val platforms: List<String>,
    )

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
                platforms = listOf("androidJvm", "common", "jvm", "js", "wasm"),
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
                platforms = listOf("common", "js", "jvm", "native", "wasm"),
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
                platforms = listOf("common", "js", "jvm", "native", "wasm"),
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

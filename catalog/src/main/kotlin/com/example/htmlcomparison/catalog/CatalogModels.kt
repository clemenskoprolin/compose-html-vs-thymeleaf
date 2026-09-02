package com.example.htmlcomparison.catalog

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

data class CatalogPage(
    val query: String,
    val projects: List<ProjectCard>,
    val status: String,
    val warning: String? = null,
    val categories: List<ProjectCategory> = emptyList(),
    val platforms: List<String> = emptyList(),
    val topTags: List<String> = emptyList(),
) {
    val isFeatured: Boolean
        get() = query.isBlank()

    /** The target groups offered as filters, so templates do not need static type access. */
    val platformFilters: List<Platform>
        get() = Platform.Filters

    fun isPlatformSelected(platform: Platform): Boolean = platform.id in platforms

    /**
     * A search link that keeps the active platform filter. Both renderers build tag and
     * navigation links through this, so their markup stays identical.
     */
    fun searchUrl(base: String, query: String): String =
        "$base?query=${query.urlEncoded()}$platformQuery"

    /** A filter link that toggles one platform and keeps the current search query. */
    fun platformUrl(base: String, platform: Platform): String {
        val nextPlatforms = if (isPlatformSelected(platform)) {
            platforms - platform.id
        } else {
            Platform.Filters.filter { it.id in platforms || it == platform }.map(Platform::id)
        }
        return "$base?query=${query.urlEncoded()}${platformQuery(nextPlatforms)}"
    }

    /** A project link that carries the current search, so the detail page can offer a way back. */
    fun projectUrl(base: String, author: String, name: String): String =
        projectPath(base, author, name) + "?$searchParameters"

    /** The active search as query parameters, for links that have to preserve it. */
    val searchParameters: String
        get() = "query=${query.urlEncoded()}$platformQuery"

    private val platformQuery: String
        get() = platformQuery(platforms)

    private fun platformQuery(platforms: List<String>): String =
        platforms.joinToString("") { "&platforms=${it.urlEncoded()}" }
}

/** The detail view for a single project, or an explanation of why it is missing. */
data class ProjectPage(
    val author: String,
    val name: String,
    val project: ProjectCard? = null,
    val readme: ProjectReadme? = null,
    val readmeNotice: String? = null,
    val tab: ProjectTab = ProjectTab.README,
    val warning: String? = null,
    val backParameters: String = "",
) {
    val tabs: List<ProjectTab>
        get() = ProjectTab.entries

    fun isSelected(tab: ProjectTab): Boolean = tab == this.tab

    fun backUrl(base: String): String =
        if (backParameters.isEmpty()) base else "$base?$backParameters"

    fun tabUrl(base: String, tab: ProjectTab): String =
        projectPath(base, author, name) + "?tab=${tab.id}" +
            if (backParameters.isEmpty()) "" else "&$backParameters"

    val klibsUrl: String
        get() = klibsProjectUrl(author, name)
}

enum class ProjectTab(val id: String, val label: String) {
    README("readme", "Readme"),
    PACKAGES("packages", "Packages"),
    ;

    companion object {
        fun of(raw: String?): ProjectTab =
            entries.firstOrNull { it.id.equals(raw?.trim(), ignoreCase = true) } ?: README
    }
}

/** A project README, already rendered from Markdown and reduced to safe HTML. */
data class ProjectReadme(
    val html: String,
    val sourceUrl: String,
    val fileName: String,
)

data class ProjectCategory(
    val title: String,
    val slug: String,
    val projects: List<RankedProject>,
) {
    val url: String
        get() = "https://klibs.io/?category=$slug"
}

data class RankedProject(
    val name: String,
    val author: String,
    val stars: String,
    val description: String = "",
    val tags: List<String> = emptyList(),
    val platforms: List<String> = emptyList(),
    val license: String = "",
    val grantWinner: Boolean = false,
) {
    val url: String
        get() = klibsProjectUrl(author, name)

    val displayedTags: List<String>
        get() = tags.take(4)

    val additionalTagCount: Int
        get() = (tags.size - displayedTags.size).coerceAtLeast(0)

    val displayedPlatforms: List<String>
        get() = platforms.take(5)
}

data class ProjectCard(
    val name: String,
    val author: String,
    val description: String,
    val url: String,
    val platforms: List<String>,
    val packages: List<ProjectPackage>,
    val totalPackages: Int,
    val targets: List<String> = emptyList(),
) {
    /** The MCP server reports raw platform ids; the catalog shows the klibs.io labels. */
    val displayedPlatforms: List<String>
        get() = platforms.take(6).map(Platform::labelOf)

    /** Packages beyond the ones the MCP server returned for this request. */
    val undisclosedPackageCount: Int
        get() = (totalPackages - packages.size).coerceAtLeast(0)

    val packageSummary: String
        get() = when {
            undisclosedPackageCount > 0 -> "${packages.size} of $totalPackages packages"
            packages.size == 1 -> "1 package"
            else -> "${packages.size} packages"
        }

    val repositoryUrl: String
        get() = "https://github.com/${author.urlEncoded()}/${name.urlEncoded()}"

    val authorUrl: String
        get() = "https://klibs.io/organization/${author.urlEncoded()}"

    /** The prefilled metadata issue klibs.io opens from its "Suggest an edit" link. */
    val suggestEditUrl: String
        get() = "https://github.com/JetBrains/klibs-io-issue-management/issues/new" +
            "?url=${url.urlEncoded()}" +
            "&title=${"[Edit project's metadata]: $name".urlEncoded()}" +
            "&labels=enhancement&template=suggest_an_edit.yml"

    /**
     * The raw target ids arrive as `GROUP` or `GROUP_target`, so they are folded back into the
     * platform groups klibs.io shows them under.
     */
    val targetGroups: List<TargetGroup>
        get() = targets
            .groupBy { it.substringBefore('_') }
            .mapNotNull { (group, ids) ->
                val platform = Platform.entries.firstOrNull { it.id.equals(group, ignoreCase = true) }
                    ?: return@mapNotNull null
                TargetGroup(
                    label = platform.label,
                    targets = ids.mapNotNull { it.substringAfter('_', "").takeIf(String::isNotBlank) }.sorted(),
                )
            }
            .sortedBy { group -> Platform.entries.indexOfFirst { it.label == group.label } }
}

data class TargetGroup(
    val label: String,
    val targets: List<String>,
)

data class ProjectPackage(
    val groupId: String,
    val artifactId: String,
    val latestVersion: String,
    val latestStableVersion: String?,
    val description: String = "",
) {
    val coordinate: String
        get() = "$groupId:$artifactId"

    val displayVersion: String
        get() = latestStableVersion ?: latestVersion

    /** Ready to paste into a Gradle build script. */
    val dependencyNotation: String
        get() = "implementation(\"$coordinate:$displayVersion\")"

    val hasPrerelease: Boolean
        get() = latestStableVersion != null && latestStableVersion != latestVersion
}

interface ReadmeGateway {
    fun readme(author: String, name: String): ProjectReadme?
}

interface KlibsGateway {
    fun searchProjects(query: String, platforms: List<String>): List<ProjectCard>

    fun findProject(author: String, name: String): ProjectCard?
}

internal fun klibsProjectUrl(author: String, name: String): String =
    "https://klibs.io/project/${author.urlEncoded()}/${name.urlEncoded()}"

internal fun projectPath(base: String, author: String, name: String): String =
    "$base/project/${author.urlEncoded()}/${name.urlEncoded()}"

internal fun String.urlEncoded(): String =
    URLEncoder.encode(this, StandardCharsets.UTF_8).replace("+", "%20")

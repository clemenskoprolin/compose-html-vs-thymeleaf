package com.example.htmlcomparison.catalog

data class CatalogPage(
    val query: String,
    val projects: List<ProjectCard>,
    val status: String,
    val warning: String? = null,
    val categories: List<ProjectCategory> = emptyList(),
) {
    val isFeatured: Boolean
        get() = query.isBlank()
}

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
        get() = "https://klibs.io/project/$author/$name"

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
) {
    val displayedPlatforms: List<String>
        get() = platforms.take(6)

    val displayedPackages: List<ProjectPackage>
        get() = packages.take(3)

    val additionalPackageCount: Int
        get() = (totalPackages - displayedPackages.size).coerceAtLeast(0)
}

data class ProjectPackage(
    val groupId: String,
    val artifactId: String,
    val latestVersion: String,
    val latestStableVersion: String?,
) {
    val coordinate: String
        get() = "$groupId:$artifactId"

    val displayVersion: String
        get() = latestStableVersion ?: latestVersion
}

fun interface KlibsGateway {
    fun searchProjects(query: String): List<ProjectCard>
}

package com.example.htmlcomparison.catalog

data class CatalogPage(
    val query: String,
    val projects: List<ProjectCard>,
    val status: String,
    val warning: String? = null,
)

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

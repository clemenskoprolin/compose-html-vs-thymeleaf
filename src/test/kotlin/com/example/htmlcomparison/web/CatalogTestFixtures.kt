package com.example.htmlcomparison.web

import com.example.htmlcomparison.catalog.ProjectCard
import com.example.htmlcomparison.catalog.ProjectPackage

internal fun projectWithOverflowingMetadata() = ProjectCard(
    name = "Example",
    author = "Example Author",
    description = "An example project",
    url = "https://klibs.io/example",
    platforms = (1..7).map { "platform-$it" },
    packages = (1..4).map { index ->
        ProjectPackage(
            groupId = "example",
            artifactId = "package-$index",
            latestVersion = "1.0.$index",
            latestStableVersion = null,
        )
    },
    totalPackages = 6,
)

package com.example.htmlcomparison.hydration

import com.example.htmlcomparison.catalog.CatalogPage

internal fun CatalogPage.toSearchState(): SearchState = SearchState(
    query = query,
    status = status,
    warning = warning,
    platforms = platformFilters.map { platform ->
        PlatformSnapshot(
            id = platform.id,
            label = platform.label,
            selected = isPlatformSelected(platform),
        )
    },
    topTags = topTags,
    projects = projects.map { project ->
        ProjectSnapshot(
            name = project.name,
            author = project.author,
            description = project.description,
            url = project.url,
            displayedPlatforms = project.displayedPlatforms,
        )
    },
    categories = categories.map { category ->
        CategorySnapshot(
            title = category.title,
            slug = category.slug,
            url = category.url,
            projects = category.projects.map { project ->
                RankedProjectSnapshot(
                    name = project.name,
                    author = project.author,
                    stars = project.stars,
                    description = project.description,
                    tags = project.tags,
                    platforms = project.platforms,
                    url = project.url,
                    grantWinner = project.grantWinner,
                )
            },
        )
    },
)

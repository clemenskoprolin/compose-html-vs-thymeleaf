package com.example.htmlcomparison.hydration

import tools.jackson.databind.json.JsonMapper

private val SearchStateJson = JsonMapper.builder().build()

fun SearchState.toJson(): String = SearchStateJson.writeValueAsString(
    linkedMapOf(
        "query" to query,
        "status" to status,
        "warning" to warning,
        "platforms" to platforms.map { platform ->
            linkedMapOf(
                "id" to platform.id,
                "label" to platform.label,
                "selected" to platform.selected,
            )
        },
        "topTags" to topTags,
        "projects" to projects.map { project -> project.toJsonValue() },
        "categories" to categories.map { category ->
            linkedMapOf(
                "title" to category.title,
                "slug" to category.slug,
                "url" to category.url,
                "projects" to category.projects.map { project -> project.toJsonValue() },
            )
        },
    ),
)

private fun ProjectSnapshot.toJsonValue(): Map<String, Any> = linkedMapOf(
    "name" to name,
    "author" to author,
    "description" to description,
    "url" to url,
    "displayedPlatforms" to displayedPlatforms,
)

private fun RankedProjectSnapshot.toJsonValue(): Map<String, Any> = linkedMapOf(
    "name" to name,
    "author" to author,
    "stars" to stars,
    "description" to description,
    "tags" to tags,
    "platforms" to platforms,
    "url" to url,
    "grantWinner" to grantWinner,
)

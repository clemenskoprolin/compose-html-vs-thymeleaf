@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package com.example.htmlcomparison.hydration

import kotlinx.browser.JsAny
import kotlinx.browser.JsArray
import kotlinx.browser.JsString
import kotlinx.browser.toJsString
import kotlinx.browser.toKotlinString
import kotlinx.browser.toList

private external interface SearchStateJson : JsAny {
    val query: JsString
    val status: JsString
    val warning: JsString?
    val platforms: JsArray<PlatformSnapshotJson>
    val topTags: JsArray<JsString>
    val projects: JsArray<ProjectSnapshotJson>
    val categories: JsArray<CategorySnapshotJson>
}

private external interface PlatformSnapshotJson : JsAny {
    val id: JsString
    val label: JsString
    val selected: Boolean
}

private external interface ProjectSnapshotJson : JsAny {
    val name: JsString
    val author: JsString
    val description: JsString
    val url: JsString
    val displayedPlatforms: JsArray<JsString>
}

private external interface CategorySnapshotJson : JsAny {
    val title: JsString
    val slug: JsString
    val url: JsString
    val projects: JsArray<RankedProjectSnapshotJson>
}

private external interface RankedProjectSnapshotJson : JsAny {
    val name: JsString
    val author: JsString
    val stars: JsString
    val description: JsString
    val tags: JsArray<JsString>
    val platforms: JsArray<JsString>
    val url: JsString
    val grantWinner: Boolean
}

@JsFun("input => JSON.parse(input)")
private external fun parseSearchState(input: JsString): SearchStateJson

fun searchStateFromJson(serialized: String): SearchState {
    val value = parseSearchState(serialized.toJsString())
    return SearchState(
        query = value.query.toKotlinString(),
        status = value.status.toKotlinString(),
        warning = value.warning?.toKotlinString(),
        platforms = value.platforms.toList().map { platform ->
            PlatformSnapshot(
                id = platform.id.toKotlinString(),
                label = platform.label.toKotlinString(),
                selected = platform.selected,
            )
        },
        topTags = value.topTags.toStringList(),
        projects = value.projects.toList().map(::projectSnapshot),
        categories = value.categories.toList().map { category ->
            CategorySnapshot(
                title = category.title.toKotlinString(),
                slug = category.slug.toKotlinString(),
                url = category.url.toKotlinString(),
                projects = category.projects.toList().map(::rankedProjectSnapshot),
            )
        },
    )
}

private fun projectSnapshot(value: ProjectSnapshotJson): ProjectSnapshot = ProjectSnapshot(
    name = value.name.toKotlinString(),
    author = value.author.toKotlinString(),
    description = value.description.toKotlinString(),
    url = value.url.toKotlinString(),
    displayedPlatforms = value.displayedPlatforms.toStringList(),
)

private fun rankedProjectSnapshot(value: RankedProjectSnapshotJson): RankedProjectSnapshot = RankedProjectSnapshot(
    name = value.name.toKotlinString(),
    author = value.author.toKotlinString(),
    stars = value.stars.toKotlinString(),
    description = value.description.toKotlinString(),
    tags = value.tags.toStringList(),
    platforms = value.platforms.toStringList(),
    url = value.url.toKotlinString(),
    grantWinner = value.grantWinner,
)

private fun JsArray<JsString>.toStringList(): List<String> =
    toList().map(JsString::toKotlinString)

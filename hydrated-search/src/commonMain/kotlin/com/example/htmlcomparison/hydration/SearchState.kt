package com.example.htmlcomparison.hydration

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * The public, immutable state transferred from the JVM renderer to the browser.
 * Keep this deliberately smaller than the server's catalog model: it is visible in page source.
 */
@Serializable
data class SearchState(
    val query: String,
    val status: String,
    val warning: String? = null,
    val platforms: List<PlatformSnapshot> = emptyList(),
    val topTags: List<String> = emptyList(),
    val projects: List<ProjectSnapshot> = emptyList(),
    val categories: List<CategorySnapshot> = emptyList(),
) {
    val isFeatured: Boolean
        get() = query.isBlank()

    val selectedPlatformIds: List<String>
        get() = platforms.filter(PlatformSnapshot::selected).map(PlatformSnapshot::id)

    fun params(query: String = this.query, platforms: List<String> = selectedPlatformIds): SearchParams =
        SearchParams(query = query, platforms = platforms)
}

@Serializable
data class PlatformSnapshot(
    val id: String,
    val label: String,
    val selected: Boolean,
)

@Serializable
data class ProjectSnapshot(
    val name: String,
    val author: String,
    val description: String,
    val url: String,
    val displayedPlatforms: List<String>,
)

@Serializable
data class CategorySnapshot(
    val title: String,
    val slug: String,
    val url: String,
    val projects: List<RankedProjectSnapshot>,
)

@Serializable
data class RankedProjectSnapshot(
    val name: String,
    val author: String,
    val stars: String,
    val description: String,
    val tags: List<String>,
    val platforms: List<String>,
    val url: String,
    val grantWinner: Boolean,
)

@Serializable
data class SearchParams(
    val query: String,
    val platforms: List<String> = emptyList(),
) {
    fun url(basePath: String): String = "$basePath?${toQueryString()}"

    fun toQueryString(): String = buildList {
        add("query=${query.trim().urlEncoded()}")
        platforms.distinct().forEach { platform -> add("platforms=${platform.urlEncoded()}") }
    }.joinToString("&")
}

private val SearchStateJson = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
}

fun SearchState.toJson(): String = SearchStateJson.encodeToString(this)

fun searchStateFromJson(serialized: String): SearchState = SearchStateJson.decodeFromString(serialized)

/** RFC 3986 encoding keeps links identical on the JVM and in the browser. */
private fun String.urlEncoded(): String = buildString {
    this@urlEncoded.encodeToByteArray().forEach { byte ->
        val value = byte.toInt() and 0xff
        val character = value.toChar()
        if (character.isUnreservedUrlCharacter()) {
            append(character)
        } else {
            append('%')
            append(HexDigits[value ushr 4])
            append(HexDigits[value and 0x0f])
        }
    }
}

private fun Char.isUnreservedUrlCharacter(): Boolean =
    this in 'a'..'z' ||
        this in 'A'..'Z' ||
        this in '0'..'9' ||
        this == '-' ||
        this == '.' ||
        this == '_' ||
        this == '~'

private const val HexDigits = "0123456789ABCDEF"

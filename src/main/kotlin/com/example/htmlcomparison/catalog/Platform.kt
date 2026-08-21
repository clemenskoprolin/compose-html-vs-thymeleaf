package com.example.htmlcomparison.catalog

/**
 * The platform ids the klibs.io MCP server accepts for `searchProjects.platforms`, paired with
 * the labels klibs.io shows for them. Selecting several platforms narrows the result to
 * projects that support all of them, which is how the MCP server treats the argument.
 */
enum class Platform(val id: String, val label: String) {
    COMMON("common", "Common"),
    ANDROID_JVM("androidJvm", "Android JVM"),
    JVM("jvm", "JVM"),
    NATIVE("native", "Kotlin/Native"),
    WASM("wasm", "Wasm"),
    JS("js", "JS"),
    ;

    companion object {
        /** The target groups klibs.io offers as filters; every multiplatform project is common. */
        val Filters: List<Platform> = entries - COMMON

        fun labelOf(id: String): String =
            entries.firstOrNull { it.id.equals(id, ignoreCase = true) }?.label ?: id

        /** Reduces a request parameter to canonical ids in declaration order, dropping unknowns. */
        fun select(raw: Collection<String>?): List<String> = Filters
            .filter { platform -> raw.orEmpty().any { it.trim().equals(platform.id, ignoreCase = true) } }
            .map(Platform::id)
    }
}

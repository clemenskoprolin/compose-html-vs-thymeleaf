package com.example.htmlcomparison.web

import com.example.htmlcomparison.catalog.CatalogPage
import com.example.htmlcomparison.catalog.ProjectCategory
import com.example.htmlcomparison.catalog.RankedProject
import com.example.htmlcomparison.web.compose.ComposeHtmlPageRenderer
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ComposeHtmlPageRendererTest {
    private val renderer = ComposeHtmlPageRenderer()

    @Test
    fun `renders a complete escaped document for the compose endpoint`() {
        val html = renderer.render(
            page = CatalogPage(
                query = "<html & css>",
                projects = emptyList(),
                status = "No results",
            ),
            formAction = "/composehtml",
            otherRendererUrl = "/thymeleaf",
        )

        assertTrue(html.startsWith("<!doctype html><html lang=\"en\">"))
        assertTrue(html.contains("data-renderer=\"compose-html\""))
        assertTrue(html.contains("data-current-renderer=\"Compose HTML\""))
        assertTrue(html.contains("aria-label=\"Switch to Thymeleaf\""), html)
        assertTrue(html.contains("src=\"/theme.js\""))
        assertTrue(html.contains("data-theme-toggle=\"true\""))
        assertTrue(html.contains("action=\"/composehtml\""))
        assertTrue(html.contains("href=\"/thymeleaf\""))
        assertTrue(html.contains("value=\"&lt;html &amp; css&gt;\""), html)
        assertFalse(html.contains("value=\"<html & css>\""))
    }

    @Test
    fun `renders only the display-ready project metadata`() {
        val html = renderer.render(
            page = CatalogPage(
                query = "html",
                projects = listOf(projectWithOverflowingMetadata()),
                status = "1 result",
            ),
            formAction = "/composehtml",
            otherRendererUrl = "/thymeleaf",
        )

        assertTrue(html.contains("platform-6"))
        assertFalse(html.contains("platform-7"))
        assertFalse(html.contains("package-1"), html)
        assertFalse(html.contains("6 packages"), html)
    }

    @Test
    fun `renders ranked categories and the grant banner for the default page`() {
        val html = renderer.render(
            page = CatalogPage(
                query = "",
                projects = emptyList(),
                status = "Ranked by stars",
                categories = listOf(
                    ProjectCategory(
                        title = "Compose UI",
                        slug = "compose-ui",
                        projects = listOf(
                            RankedProject(
                                name = "compose-multiplatform",
                                author = "JetBrains",
                                stars = "19.3k",
                                description = "Share declarative interfaces across platforms.",
                                tags = listOf("#compose-ui", "#compose"),
                                platforms = listOf("Android JVM", "Kotlin/Native", "Wasm"),
                                license = "Apache License 2.0",
                            ),
                        ),
                    ),
                    ProjectCategory(
                        title = "Local Storage",
                        slug = "local-storage",
                        projects = listOf(
                            RankedProject("Store", "MobileNativeFoundation", "3.4k", grantWinner = true),
                        ),
                    ),
                ),
            ),
            formAction = "/composehtml",
            otherRendererUrl = "/thymeleaf",
        )

        assertTrue(html.contains("data-category=\"compose-ui\""), html)
        assertTrue(html.contains("compose-multiplatform"), html)
        assertTrue(html.contains("data-star-count=\"19.3k\""), html)
        assertTrue(html.contains("Share declarative interfaces across platforms."), html)
        assertTrue(html.contains("#compose-ui"), html)
        assertTrue(html.contains("Kotlin/Native"), html)
        assertFalse(html.contains("Apache License 2.0"), html)
        assertTrue(html.contains("Kotlin Grant Winners"), html)
        assertTrue(html.indexOf("Compose UI") < html.indexOf("Kotlin Grant Winners"), html)
        assertTrue(html.indexOf("Kotlin Grant Winners") < html.indexOf("Local Storage"), html)
    }

    @Test
    fun `renders a GET search form with immediately applied platform links`() {
        val html = renderer.render(
            page = CatalogPage(
                query = "compose",
                projects = emptyList(),
                status = "No projects found",
                platforms = listOf("wasm"),
                topTags = listOf("compose", "apple"),
            ),
            formAction = "/composehtml",
            otherRendererUrl = "/thymeleaf",
        )

        assertTrue(html.contains("id=\"catalog-results\""), html)
        assertTrue(html.contains("action=\"/composehtml\""), html)
        assertTrue(html.contains("method=\"get\""), html)
        assertFalse(html.contains("/app.js"), html)
        // The form keeps the filter for text searches, while filter links toggle immediately.
        assertTrue(html.contains("type=\"hidden\" name=\"platforms\" value=\"wasm\""), html)
        assertFalse(html.contains("type=\"checkbox\""), html)
        assertTrue(html.contains("href=\"/composehtml?query=compose\""), html)
        assertTrue(html.contains("href=\"/composehtml?query=compose&amp;platforms=jvm&amp;platforms=wasm\""), html)
        assertTrue(html.contains("href=\"/composehtml?query=apple&amp;platforms=wasm\""), html)
    }
}

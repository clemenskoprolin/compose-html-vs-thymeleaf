package com.example.htmlcomparison.web

import com.example.htmlcomparison.catalog.CatalogPage
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
        assertTrue(html.contains("Current renderer: Compose HTML. Switch to Thymeleaf"), html)
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
        assertTrue(html.contains("example:package-3"))
        assertFalse(html.contains("example:package-4"))
        assertTrue(html.contains("+3 more packages"))
    }
}

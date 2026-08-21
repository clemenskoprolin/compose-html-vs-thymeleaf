package com.example.htmlcomparison.web

import com.example.htmlcomparison.catalog.CatalogPage
import com.example.htmlcomparison.web.compose.ComposeHtmlPageRenderer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * The two renderers have to produce the same document. Only the renderer identity may
 * differ, so it is folded into a placeholder before the structural comparison.
 */
class RendererParityTest {
    private val composeHtmlPageRenderer = ComposeHtmlPageRenderer()

    @Test
    fun `both renderers agree on the featured page`() {
        assertSameDocument(
            CatalogPage(
                query = "",
                projects = emptyList(),
                status = "Ranked by stars",
                categories = featuredTestCategories(),
            )
        )
    }

    @Test
    fun `both renderers agree on a search result page`() {
        assertSameDocument(
            CatalogPage(
                query = "html",
                projects = listOf(projectWithOverflowingMetadata()),
                status = "1 project found",
            )
        )
    }

    @Test
    fun `both renderers agree on an empty search with a warning`() {
        assertSameDocument(
            CatalogPage(
                query = "android",
                projects = emptyList(),
                status = "No projects found.",
                warning = "The live klibs.io MCP search is temporarily unavailable.",
            )
        )
    }

    private fun assertSameDocument(page: CatalogPage) {
        val composeHtml = composeHtmlPageRenderer.render(page, FORM_ACTION, OTHER_RENDERER_URL)
        val thymeleafHtml = ThymeleafTestRenderer.render(page, FORM_ACTION, OTHER_RENDERER_URL)

        assertEquals(
            HtmlNormalizer.normalize(withoutRendererIdentity(composeHtml)),
            HtmlNormalizer.normalize(withoutRendererIdentity(thymeleafHtml)),
        )
    }

    /**
     * The body marker and the switcher are the only places where a page is allowed to know
     * which renderer produced it; each renderer's own test covers them.
     */
    private fun withoutRendererIdentity(html: String): String = html
        .replace("data-renderer=\"compose-html\"", RENDERER_PLACEHOLDER)
        .replace("data-renderer=\"thymeleaf\"", RENDERER_PLACEHOLDER)
        .replace(RENDERER_SWITCH, RENDERER_PLACEHOLDER)

    private companion object {
        const val FORM_ACTION = "/renderer"
        const val OTHER_RENDERER_URL = "/other-renderer"
        const val RENDERER_PLACEHOLDER = "a-renderer"
        val RENDERER_SWITCH =
            Regex("""<div[^>]*aria-label="Server-side HTML renderer"[^>]*>.*?</div>""", RegexOption.DOT_MATCHES_ALL)
    }
}

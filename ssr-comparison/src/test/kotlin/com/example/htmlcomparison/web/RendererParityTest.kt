package com.example.htmlcomparison.web

import com.example.htmlcomparison.catalog.CatalogPage
import com.example.htmlcomparison.catalog.ProjectPage
import com.example.htmlcomparison.catalog.ProjectReadme
import com.example.htmlcomparison.catalog.ProjectTab
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

    @Test
    fun `both renderers agree on a platform-filtered search page`() {
        assertSameDocument(
            CatalogPage(
                query = "html",
                projects = listOf(projectWithOverflowingMetadata()),
                status = "1 project found on JVM, Wasm",
                platforms = listOf("jvm", "wasm"),
                topTags = listOf("compose", "html"),
            )
        )
    }

    @Test
    fun `both renderers agree on a project detail page`() {
        assertSameProject(
            ProjectPage(
                author = "JetBrains",
                name = "compose-multiplatform",
                project = detailedTestProject(),
            )
        )
    }

    @Test
    fun `both renderers agree on a rendered readme`() {
        // Compose replays the markup as composables while Thymeleaf writes it with th:utext.
        assertSameProject(
            ProjectPage(
                author = "JetBrains",
                name = "compose-multiplatform",
                project = detailedTestProject(),
                readme = ProjectReadme(
                    html = """<h1>Compose Multiplatform</h1><p>Share <strong>UI</strong> code &amp; more.</p>""" +
                        """<ul><li><a href="https://example.com" target="_blank" rel="noreferrer">iOS</a></li></ul>""" +
                        """<pre><code class="language-kotlin">fun main() {\n    println("hi")\n}\n</code></pre>""" +
                        """<img src="https://example.com/shot.png" alt="Screen shot" loading="lazy">""" +
                        """<table><thead><tr><th>Target</th></tr></thead><tbody><tr><td>JVM</td></tr></tbody></table>""",
                    sourceUrl = "https://github.com/JetBrains/compose-multiplatform/blob/HEAD/README.md",
                    fileName = "README.md",
                ),
            )
        )
    }

    @Test
    fun `both renderers agree on the packages tab`() {
        assertSameProject(
            ProjectPage(
                author = "JetBrains",
                name = "compose-multiplatform",
                project = detailedTestProject(),
                tab = ProjectTab.PACKAGES,
                backParameters = "query=compose&platforms=wasm",
            )
        )
    }

    @Test
    fun `both renderers agree on a project the MCP server does not know`() {
        assertSameProject(
            ProjectPage(
                author = "Nobody",
                name = "nothing",
                warning = "klibs.io has no project called \u201cNobody/nothing\u201d.",
            )
        )
    }

    private fun assertSameProject(projectPage: ProjectPage) {
        val composeHtml = composeHtmlPageRenderer
            .renderProject(projectPage, FORM_ACTION, OTHER_RENDERER_URL)
        val thymeleafHtml = ThymeleafTestRenderer
            .renderProject(projectPage, FORM_ACTION, OTHER_RENDERER_URL)

        assertEquals(
            HtmlNormalizer.normalize(withoutRendererIdentity(composeHtml)),
            HtmlNormalizer.normalize(withoutRendererIdentity(thymeleafHtml)),
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

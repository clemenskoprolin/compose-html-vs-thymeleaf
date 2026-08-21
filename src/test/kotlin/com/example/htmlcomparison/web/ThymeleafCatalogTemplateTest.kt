package com.example.htmlcomparison.web

import com.example.htmlcomparison.catalog.CatalogPage
import com.example.htmlcomparison.catalog.ProjectCategory
import com.example.htmlcomparison.catalog.RankedProject
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

class ThymeleafCatalogTemplateTest {
    private val templateEngine = SpringTemplateEngine().apply {
        setTemplateResolver(
            ClassLoaderTemplateResolver().apply {
                prefix = "templates/"
                suffix = ".html"
                templateMode = TemplateMode.HTML
                characterEncoding = Charsets.UTF_8.name()
                isCacheable = false
            }
        )
    }

    @Test
    fun `resolves the component fragments and display-ready metadata`() {
        val context = Context().apply {
            setVariable(
                "page",
                CatalogPage(
                    query = "<html & css>",
                    projects = listOf(projectWithOverflowingMetadata()),
                    status = "1 result",
                ),
            )
            setVariable("formAction", "/thymeleaf")
            setVariable("otherRendererUrl", "/composehtml")
        }

        val html = templateEngine.process("catalog", context)

        assertTrue(html.contains("data-renderer=\"thymeleaf\""))
        assertTrue(html.contains("data-current-renderer=\"Thymeleaf\""))
        assertTrue(html.contains("Current renderer: Thymeleaf. Switch to Compose HTML"), html)
        assertTrue(html.contains("data-theme-toggle=\"true\""))
        assertTrue(html.contains("action=\"/thymeleaf\""))
        assertTrue(html.contains("href=\"/composehtml\""))
        assertTrue(html.contains("value=\"&lt;html &amp; css&gt;\""), html)
        assertTrue(html.contains("platform-6"))
        assertFalse(html.contains("platform-7"))
        assertTrue(html.contains("example:package-3"))
        assertFalse(html.contains("example:package-4"))
        assertTrue(html.contains("+<span>3</span> more packages"), html)
    }

    @Test
    fun `renders ranked categories and the grant banner for the default page`() {
        val context = Context().apply {
            setVariable(
                "page",
                CatalogPage(
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
            )
            setVariable("formAction", "/thymeleaf")
            setVariable("otherRendererUrl", "/composehtml")
        }

        val html = templateEngine.process("catalog", context)

        assertTrue(html.contains("data-category=\"compose-ui\""), html)
        assertTrue(html.contains("compose-multiplatform"), html)
        assertTrue(html.contains("data-star-count=\"19.3k\""), html)
        assertTrue(html.contains("Share declarative interfaces across platforms."), html)
        assertTrue(html.contains("#compose-ui"), html)
        assertTrue(html.contains("Kotlin/Native"), html)
        assertFalse(html.contains(">Platforms<"), html)
        assertFalse(html.contains("Apache License 2.0"), html)
        assertTrue(html.contains("Kotlin Grant Winners"), html)
        assertTrue(html.indexOf("Compose UI") < html.indexOf("Kotlin Grant Winners"), html)
        assertTrue(html.indexOf("Kotlin Grant Winners") < html.indexOf("Local Storage"), html)
    }
}

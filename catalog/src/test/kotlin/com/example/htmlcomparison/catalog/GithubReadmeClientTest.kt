package com.example.htmlcomparison.catalog

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.net.http.HttpClient

class GithubReadmeClientTest {
    private val client = GithubReadmeClient(HttpClient.newHttpClient(), timeoutSeconds = 1)

    @Test
    fun `renders markdown including tables and fenced code`() {
        val html = client.toSafeHtml(
            markdown = """
                # Title

                Some **bold** text with `code`.

                | Target | Since |
                | ------ | ----- |
                | JVM    | 1.0   |

                ```kotlin
                fun main() = Unit
                ```
            """.trimIndent(),
            author = "JetBrains",
            name = "compose-multiplatform",
        )

        assertTrue(html.contains("<h1>Title</h1>"), html)
        assertTrue(html.contains("<strong>bold</strong>"), html)
        assertTrue(html.contains("<code>code</code>"), html)
        assertTrue(html.contains("<table>"), html)
        assertTrue(html.contains("fun main() = Unit"), html)
    }

    @Test
    fun `drops markup that a README must not bring onto the page`() {
        val html = client.toSafeHtml(
            markdown = """
                <script>alert(1)</script>
                <img src="x" onerror="alert(2)">
                <a href="javascript:alert(3)">click</a>
                <iframe src="https://example.com"></iframe>
            """.trimIndent(),
            author = "JetBrains",
            name = "compose-multiplatform",
        )

        assertFalse(html.contains("script"), html)
        assertFalse(html.contains("onerror"), html)
        assertFalse(html.contains("javascript:"), html)
        assertFalse(html.contains("iframe"), html)
    }

    @Test
    fun `resolves repository-relative images and links`() {
        val html = client.toSafeHtml(
            markdown = "![shot](docs/shot.png) and [guide](docs/guide.md) and [home](https://example.com)",
            author = "JetBrains",
            name = "compose-multiplatform",
        )

        assertTrue(
            html.contains("https://raw.githubusercontent.com/JetBrains/compose-multiplatform/HEAD/docs/shot.png"),
            html,
        )
        assertTrue(
            html.contains("https://github.com/JetBrains/compose-multiplatform/blob/HEAD/docs/guide.md"),
            html,
        )
        assertTrue(html.contains("https://example.com"), html)
        // Every link leaves the prototype in a new tab without leaking the referrer.
        assertEquals(3, Regex("rel=\"noreferrer\"").findAll(html).count() + Regex("loading=\"lazy\"").findAll(html).count())
    }
}

package com.example.htmlcomparison.web.compose.components.project

import androidx.compose.runtime.Composable
import kotlinx.browser.dom.HTMLElement
import org.jetbrains.compose.web.dom.TagElement
import org.jetbrains.compose.web.dom.Text
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

/**
 * Compose HTML's string renderer escapes every text node and ignores `prop(innerHTML)`, so
 * markup that was rendered elsewhere has to be replayed as composables. Thymeleaf writes the
 * same string with `th:utext`, and both renderers end up with the same document.
 */
@Composable
internal fun RawHtml(html: String) {
    val fragment = Jsoup.parseBodyFragment(html)
    fragment.outputSettings().prettyPrint(false)
    fragment.body().childNodes().forEach { node -> RawNode(node) }
}

@Composable
private fun RawNode(node: Node) {
    when (node) {
        is TextNode -> Text(node.wholeText)
        is Element -> TagElement<HTMLElement>(
            tagName = node.tagName(),
            applyAttrs = { node.attributes().forEach { attribute -> attr(attribute.key, attribute.value) } },
            content = { node.childNodes().forEach { child -> RawNode(child) } },
        )
        // Comments and doctypes never survive the sanitizer, so nothing else can appear here.
        else -> Unit
    }
}

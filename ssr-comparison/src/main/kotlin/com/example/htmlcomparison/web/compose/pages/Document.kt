package com.example.htmlcomparison.web.compose.pages

import androidx.compose.runtime.Composable
import kotlinx.browser.dom.HTMLElement
import org.jetbrains.compose.web.attributes.LinkRel
import org.jetbrains.compose.web.attributes.href
import org.jetbrains.compose.web.attributes.rel
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Head
import org.jetbrains.compose.web.dom.Link
import org.jetbrains.compose.web.dom.Meta
import org.jetbrains.compose.web.dom.TagElement
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Title

private const val SITE_TITLE = "KMP Library Finder"

@Composable
internal fun DocumentHead(subject: String? = null) {
    Head {
        Meta { attr("charset", "UTF-8") }
        Meta {
            attr("name", "viewport")
            attr("content", "width=device-width, initial-scale=1")
        }
        Title { Text(if (subject.isNullOrBlank()) SITE_TITLE else "$subject — $SITE_TITLE") }
        Link {
            rel(LinkRel.Stylesheet)
            href("/app.css")
        }
        ExternalScript("/theme.js")
    }
}

@Composable
internal fun PageBackdrop() {
    Div({ classes("page-glow", "fixed", "inset-0", "-z-10") })
}

@Composable
private fun ExternalScript(src: String) {
    TagElement<HTMLElement>(
        tagName = "script",
        applyAttrs = { attr("src", src) },
        content = {},
    )
}

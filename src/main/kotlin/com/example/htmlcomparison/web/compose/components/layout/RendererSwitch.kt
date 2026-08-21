package com.example.htmlcomparison.web.compose.components.layout

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

/**
 * Both renderers keep the same position in the control, so only the highlight moves when
 * switching. The link carries the active query, so a search survives the switch.
 */
private val RendererOrder = listOf("Compose HTML", "Thymeleaf")

@Composable
internal fun RendererSwitch(
    otherRendererUrl: String,
    currentRenderer: String,
) {
    Div({
        classes("inline-flex", "shrink-0", "items-center", "gap-1", "rounded-full", "border", "border-line-strong", "bg-surface", "p-1")
        attr("role", "group")
        attr("aria-label", "Server-side HTML renderer")
    }) {
        RendererOrder.forEach { renderer ->
            if (renderer == currentRenderer) {
                CurrentRenderer(renderer)
            } else {
                OtherRenderer(renderer, otherRendererUrl)
            }
        }
    }
}

@Composable
private fun CurrentRenderer(renderer: String) {
    Span({
        classes("rounded-full", "bg-accent-solid", "px-3", "py-1.5", "text-xs", "font-bold", "text-accent-ink")
        attr("aria-current", "page")
        attr("data-current-renderer", renderer)
    }) {
        Text(renderer)
    }
}

@Composable
private fun OtherRenderer(
    renderer: String,
    otherRendererUrl: String,
) {
    A(
        href = otherRendererUrl,
        attrs = {
            classes("rounded-full", "px-3", "py-1.5", "text-xs", "font-semibold", "text-muted", "no-underline", "transition", "hover:text-primary")
            attr("data-other-renderer", "true")
            attr("aria-label", "Switch to $renderer")
        },
    ) {
        Text(renderer)
    }
}

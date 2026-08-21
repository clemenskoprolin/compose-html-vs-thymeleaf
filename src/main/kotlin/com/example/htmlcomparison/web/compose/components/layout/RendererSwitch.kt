package com.example.htmlcomparison.web.compose.components.layout

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
internal fun RendererSwitch(
    otherRendererUrl: String,
    currentRenderer: String,
    otherRenderer: String,
) {
    A(
        href = otherRendererUrl,
        attrs = {
            classes("inline-flex", "items-center", "gap-2", "rounded-full", "border", "border-line-strong", "bg-surface", "px-3", "py-2", "text-xs", "font-semibold", "text-muted", "no-underline", "transition", "hover:border-accent")
            attr("data-other-renderer", "true")
            attr("data-current-renderer", currentRenderer)
            attr("aria-label", "Current renderer: $currentRenderer. Switch to $otherRenderer")
        },
    ) {
        Span({ classes("hidden", "text-subtle", "md:inline") }) { Text("Renderer") }
        Span({ classes("text-primary") }) { Text(currentRenderer) }
        Span({
            classes("text-subtle")
            attr("aria-hidden", "true")
        }) {
            Text("\u2192")
        }
        Span({ classes("text-accent") }) { Text(otherRenderer) }
    }
}

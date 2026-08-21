package com.example.htmlcomparison.web.compose.components.layout

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Header
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
internal fun SiteHeader(
    homeUrl: String,
    otherRendererUrl: String,
    currentRenderer: String,
    otherRenderer: String,
) {
    Header({ classes("border-b", "border-line") }) {
        Div({ classes("mx-auto", "flex", "max-w-6xl", "items-center", "justify-between", "gap-3", "px-6", "py-5") }) {
            BrandLink(homeUrl)
            HeaderActions(otherRendererUrl, currentRenderer, otherRenderer)
        }
    }
}

@Composable
private fun BrandLink(homeUrl: String) {
    A(
        href = homeUrl,
        attrs = { classes("flex", "min-w-0", "items-center", "gap-3", "text-primary", "no-underline") },
    ) {
        Span({ classes("grid", "h-9", "w-9", "shrink-0", "place-items-center", "rounded-xl", "bg-accent-solid", "font-black", "text-accent-ink") }) {
            Text("K")
        }
        Div {
            P({ classes("text-sm", "font-bold", "tracking-wide") }) { Text("KLIBS LAB") }
            P({ classes("hidden", "text-xs", "text-subtle", "sm:block") }) { Text("Server-rendered explorer") }
        }
    }
}

@Composable
private fun HeaderActions(
    otherRendererUrl: String,
    currentRenderer: String,
    otherRenderer: String,
) {
    Div({ classes("flex", "shrink-0", "items-center", "gap-2") }) {
        ThemeToggle()
        RendererSwitch(otherRendererUrl, currentRenderer, otherRenderer)
    }
}

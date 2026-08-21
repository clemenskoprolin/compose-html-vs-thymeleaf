package com.example.htmlcomparison.web.compose.components.layout

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Footer
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

@Composable
internal fun SiteFooter() {
    Footer({ classes("border-t", "border-line") }) {
        Div({ classes("mx-auto", "flex", "max-w-6xl", "flex-col", "gap-2", "px-6", "py-8", "text-xs", "text-subtle", "sm:flex-row", "sm:items-center", "sm:justify-between") }) {
            P { Text("One Spring model. Two server-side HTML renderers.") }
            P { Text("Live data: klibs.io MCP / searchProjects") }
        }
    }
}

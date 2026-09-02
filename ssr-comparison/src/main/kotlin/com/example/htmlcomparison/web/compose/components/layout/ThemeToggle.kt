package com.example.htmlcomparison.web.compose.components.layout

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
internal fun ThemeToggle() {
    Button(attrs = {
        classes("inline-flex", "items-center", "gap-2", "rounded-full", "border", "border-line-strong", "bg-surface", "px-3", "py-2", "text-xs", "font-semibold", "text-muted", "transition", "hover:border-accent", "hover:text-primary")
        type(ButtonType.Button)
        attr("data-theme-toggle", "true")
        attr("aria-label", "Toggle color theme")
        title("Toggle color theme")
    }) {
        Span({
            attr("data-theme-toggle-icon", "true")
            attr("aria-hidden", "true")
        }) {
            Text("\u263e")
        }
        Span({
            classes("hidden", "md:inline")
            attr("data-theme-toggle-label", "true")
        }) {
            Text("Theme")
        }
    }
}

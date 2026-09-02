package com.example.htmlcomparison.web.compose.components.feedback

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
internal fun WarningBanner(message: String?) {
    if (message == null) return

    Div({
        classes("mt-8", "rounded-2xl", "border", "border-warning-line", "bg-warning-soft", "px-5", "py-4", "text-sm", "text-warning")
        attr("role", "status")
    }) {
        Text(message)
    }
}

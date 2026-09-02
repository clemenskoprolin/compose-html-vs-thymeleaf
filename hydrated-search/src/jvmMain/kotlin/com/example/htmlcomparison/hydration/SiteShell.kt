package com.example.htmlcomparison.hydration

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Footer
import org.jetbrains.compose.web.dom.Header
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

private const val PagePath = "/"

@Composable
internal fun PageBackdrop() {
    Div({ classes("page-glow", "fixed", "inset-0", "-z-10") })
}

@Composable
internal fun SiteHeader(ssrComparisonUrl: String) {
    Header({ classes("border-b", "border-line") }) {
        Div({ classes("mx-auto", "flex", "max-w-6xl", "items-center", "justify-between", "gap-3", "px-6", "py-5") }) {
            A(
                href = PagePath,
                attrs = { classes("flex", "min-w-0", "items-center", "gap-3", "text-primary", "no-underline") },
            ) {
                Span({ classes("brand-plate", "grid", "h-9", "shrink-0", "place-items-center", "rounded-xl", "px-3") }) {
                    Img(src = "/assets/klibs-logo.svg", alt = "Klibs.io") {
                        classes("h-4", "w-auto")
                        attr("width", "124")
                        attr("height", "21")
                    }
                }
                P({ classes("hidden", "text-xs", "text-subtle", "sm:block") }) { Text("Hydrated explorer") }
            }
            Div({ classes("flex", "shrink-0", "items-center", "gap-2") }) {
                ThemeToggle()
                A(
                    href = ssrComparisonUrl,
                    attrs = {
                        classes("inline-flex", "items-center", "rounded-full", "border", "border-line-strong", "bg-surface", "px-3", "py-2", "text-xs", "font-semibold", "text-muted", "no-underline", "transition", "hover:border-accent", "hover:text-primary")
                    },
                ) { Text("SSR comparison") }
            }
        }
    }
}

@Composable
private fun ThemeToggle() {
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
        }) { Text("\u263e") }
        Span({
            classes("hidden", "md:inline")
            attr("data-theme-toggle-label", "true")
        }) { Text("Theme") }
    }
}

@Composable
internal fun SiteFooter() {
    Footer({ classes("border-t", "border-line") }) {
        Div({ classes("mx-auto", "flex", "max-w-6xl", "flex-col", "gap-2", "px-6", "py-8", "text-xs", "text-subtle", "sm:flex-row", "sm:items-center", "sm:justify-between") }) {
            P { Text("Server-rendered first. Interactive after Compose hydration.") }
            P { Text("Live data: klibs.io MCP / searchProjects") }
        }
    }
}

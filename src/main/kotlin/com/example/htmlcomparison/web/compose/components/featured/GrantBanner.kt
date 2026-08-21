package com.example.htmlcomparison.web.compose.components.featured

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
internal fun GrantBanner() {
    Section({ classes("grant-banner", "mt-14", "overflow-hidden", "rounded-3xl", "border", "border-accent-line", "p-7", "sm:mt-20") }) {
        Div({ classes("max-w-2xl") }) {
            P({ classes("mb-5", "text-xs", "font-bold", "uppercase", "tracking-[0.2em]", "text-accent") }) {
                Text("Kotlin Foundation")
            }
            H2({ classes("text-4xl", "font-black", "leading-tight", "tracking-tight", "text-primary") }) {
                Text("Kotlin Grant Winners")
            }
            P({ classes("mt-4", "text-base", "leading-6", "text-muted") }) {
                Text("Explore top-tier projects funded for pushing Multiplatform development forward.")
            }
            Div({ classes("mt-8", "flex", "flex-wrap", "gap-3") }) {
                GrantLink("https://klibs.io/?category=grant-winners", "Discover projects", primary = true)
                GrantLink("https://kotlinfoundation.org/grants/", "Learn about grants", primary = false)
            }
        }
    }
}

@Composable
private fun GrantLink(href: String, label: String, primary: Boolean) {
    A(
        href = href,
        attrs = {
            classes("inline-flex", "items-center", "gap-2", "rounded-full", "border", if (primary) "border-accent" else "border-line-strong", if (primary) "bg-accent-solid" else "bg-surface", "px-5", "py-2.5", "text-sm", "font-bold", if (primary) "text-accent-ink" else "text-primary", "no-underline", "transition", "hover:-translate-y-0.5")
        },
    ) {
        Text(label)
        Span({ attr("aria-hidden", "true") }) { Text("\u2192") }
    }
}

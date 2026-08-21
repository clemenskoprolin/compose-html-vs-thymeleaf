package com.example.htmlcomparison.web.compose.components.catalog

import androidx.compose.runtime.Composable
import com.example.htmlcomparison.catalog.ProjectCard
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

/** A live-search result using the same compact information hierarchy as the home cards. */
@Composable
internal fun ProjectResultCard(
    project: ProjectCard,
    href: String,
) {
    // The whole card opens the detail view; klibs.io stays one click further in.
    A(
        href = href,
        attrs = {
            classes("group", "flex", "h-full", "min-w-0", "flex-col", "justify-between", "gap-5", "rounded-2xl", "border", "border-line", "bg-card", "p-4", "no-underline", "transition", "hover:-translate-y-0.5", "hover:border-accent-line", "hover:bg-card-hover")
        },
    ) {
        Div {
            ProjectHeading(project)
            if (project.description.isNotBlank()) {
                P({ classes("project-card-description", "mt-3", "text-sm", "leading-6", "text-muted") }) {
                    Text(project.description)
                }
            }
            ProjectPlatforms(project)
        }
        P({ classes("flex", "items-center", "justify-between", "text-xs", "font-bold", "text-accent") }) {
            Text("View details")
            Span({ attr("aria-hidden", "true") }) { Text("↗") }
        }
    }
}

@Composable
private fun ProjectHeading(project: ProjectCard) {
    Div({ classes("min-w-0") }) {
        P({ classes("text-xs", "font-semibold", "text-subtle") }) { Text("by ${project.author}") }
        H3({ classes("mt-2", "break-words", "text-lg", "font-black", "text-primary") }) { Text(project.name) }
    }
}

@Composable
private fun ProjectPlatforms(project: ProjectCard) {
    if (project.displayedPlatforms.isEmpty()) return

    Div({ classes("mt-4") }) {
        Div({ classes("flex", "flex-wrap", "gap-1.5") }) {
            project.displayedPlatforms.forEach { platform ->
                Span({ classes("rounded-md", "border", "border-line", "bg-surface", "px-2", "py-1", "text-[10px]", "font-semibold", "text-muted") }) {
                    Text(platform)
                }
            }
        }
    }
}

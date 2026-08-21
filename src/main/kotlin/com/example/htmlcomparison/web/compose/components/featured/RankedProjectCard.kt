package com.example.htmlcomparison.web.compose.components.featured

import androidx.compose.runtime.Composable
import com.example.htmlcomparison.catalog.RankedProject
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
internal fun RankedProjectCard(
    project: RankedProject,
    href: String,
) {
    A(
        href = href,
        attrs = {
            classes("group", "flex", "h-full", "min-w-0", "flex-col", "justify-between", "gap-5", "rounded-2xl", "border", if (project.grantWinner) "border-accent-line" else "border-line", "bg-card", "p-4", "no-underline", "transition", "hover:-translate-y-0.5", "hover:border-accent-line", "hover:bg-card-hover")
            attr("data-star-count", project.stars)
        },
    ) {
        Div {
            ProjectHeading(project)
            if (project.description.isNotBlank()) {
                P({ classes("project-card-description", "mt-3", "text-sm", "leading-6", "text-muted") }) { Text(project.description) }
            }
            if (project.grantWinner) {
                P({ classes("mt-3", "inline-flex", "rounded-full", "bg-secondary-soft", "px-2.5", "py-1", "text-[11px]", "font-bold", "uppercase", "tracking-wide", "text-secondary") }) {
                    Text("Kotlin Grant winner")
                }
            }
            ProjectTags(project)
            ProjectMetadata(project)
        }
        P({ classes("flex", "items-center", "justify-between", "text-xs", "font-bold", "text-accent") }) {
            Text("View details")
            Span({ attr("aria-hidden", "true") }) { Text("\u2197") }
        }
    }
}

@Composable
private fun ProjectHeading(project: RankedProject) {
    Div({ classes("flex", "items-start", "justify-between", "gap-4") }) {
        Div({ classes("min-w-0") }) {
            P({ classes("text-xs", "font-semibold", "text-subtle") }) { Text("by ${project.author}") }
            H3({ classes("mt-2", "break-words", "text-lg", "font-black", "text-primary") }) { Text(project.name) }
        }
        P({ classes("inline-flex", "shrink-0", "items-center", "gap-1", "rounded-full", "bg-code", "px-2.5", "py-1", "text-xs", "font-bold", "text-muted") }) {
            Span({
                classes("text-accent")
                attr("aria-hidden", "true")
            }) { Text("\u2605") }
            Text(project.stars)
        }
    }
}

@Composable
private fun ProjectTags(project: RankedProject) {
    if (project.displayedTags.isEmpty()) return

    Div({ classes("mt-3", "flex", "flex-wrap", "gap-1.5") }) {
        project.displayedTags.forEach { tag ->
            Span({ classes("rounded-full", "bg-secondary-soft", "px-2.5", "py-1", "text-[11px]", "font-bold", "text-secondary") }) { Text(tag) }
        }
        if (project.additionalTagCount > 0) {
            Span({ classes("px-1", "py-1", "text-[11px]", "font-bold", "text-subtle") }) { Text("+${project.additionalTagCount}") }
        }
    }
}

@Composable
private fun ProjectMetadata(project: RankedProject) {
    if (project.displayedPlatforms.isEmpty()) return

    Div({ classes("mt-4") }) {
        Div({ classes("flex", "flex-wrap", "gap-1.5") }) {
            project.displayedPlatforms.forEach { platform ->
                Span({ classes("rounded-md", "border", "border-line", "bg-surface", "px-2", "py-1", "text-[10px]", "font-semibold", "text-muted") }) { Text(platform) }
            }
        }
    }
}

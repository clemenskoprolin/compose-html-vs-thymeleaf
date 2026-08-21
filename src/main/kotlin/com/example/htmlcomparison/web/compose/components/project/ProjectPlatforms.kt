package com.example.htmlcomparison.web.compose.components.project

import androidx.compose.runtime.Composable
import com.example.htmlcomparison.catalog.ProjectCard
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
internal fun ProjectPlatforms(project: ProjectCard) {
    if (project.displayedPlatforms.isEmpty()) return

    Div({
        classes("flex", "flex-wrap", "gap-2")
        attr("aria-label", "Platforms")
    }) {
        project.displayedPlatforms.forEach { platform ->
            Span({ classes("rounded-full", "bg-secondary-soft", "px-2.5", "py-1", "text-[11px]", "font-bold", "uppercase", "tracking-wide", "text-secondary") }) {
                Text(platform)
            }
        }
    }
}

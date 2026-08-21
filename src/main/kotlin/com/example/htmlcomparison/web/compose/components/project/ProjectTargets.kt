package com.example.htmlcomparison.web.compose.components.project

import androidx.compose.runtime.Composable
import com.example.htmlcomparison.catalog.ProjectCard
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

/** The published Kotlin targets, folded back into the platform groups klibs.io lists them under. */
@Composable
internal fun ProjectTargets(project: ProjectCard) {
    val groups = project.targetGroups
    if (groups.isEmpty()) return

    Section({ classes("rounded-3xl", "border", "border-line", "bg-card", "p-6") }) {
        H2({ classes("text-sm", "font-bold", "uppercase", "tracking-[0.16em]", "text-subtle") }) { Text("Targets") }
        Div({ classes("mt-4", "space-y-4") }) {
            groups.forEach { group ->
                Div {
                    P({ classes("text-xs", "font-bold", "uppercase", "tracking-[0.16em]", "text-subtle") }) {
                        Text(group.label)
                    }
                    if (group.targets.isEmpty()) {
                        P({ classes("mt-2", "text-sm", "text-muted") }) { Text("Published without a specific target.") }
                    } else {
                        Div({ classes("mt-2", "flex", "flex-wrap", "gap-1.5") }) {
                            group.targets.forEach { target ->
                                Span({ classes("rounded-md", "border", "border-line", "bg-surface", "px-2", "py-1", "text-[11px]", "font-semibold", "text-muted") }) {
                                    Text(target)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

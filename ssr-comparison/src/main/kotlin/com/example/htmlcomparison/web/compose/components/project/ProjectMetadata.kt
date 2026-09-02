package com.example.htmlcomparison.web.compose.components.project

import androidx.compose.runtime.Composable
import com.example.htmlcomparison.catalog.ProjectCard
import org.jetbrains.compose.web.attributes.ARel
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.rel
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

/**
 * klibs.io also shows stars, license and activity dates here. The MCP server does not expose
 * those, so the panel says so rather than inventing them.
 */
@Composable
internal fun ProjectMetadata(project: ProjectCard) {
    Section({ classes("rounded-3xl", "border", "border-line", "bg-card", "p-6") }) {
        H2({ classes("text-sm", "font-bold", "uppercase", "tracking-[0.16em]", "text-subtle") }) { Text("Metadata") }
        Div({ classes("mt-4", "space-y-3", "text-sm") }) {
            MetadataRow("Author") {
                A(
                    href = project.authorUrl,
                    attrs = {
                        classes("font-bold", "text-accent", "no-underline", "transition", "hover:text-primary")
                        target(ATarget.Blank)
                        rel(ARel.NoReferrer)
                    },
                ) {
                    Text(project.author)
                }
            }
            MetadataRow("Platforms") { Span({ classes("font-bold", "text-primary") }) { Text(project.displayedPlatforms.size.toString()) } }
            MetadataRow("Targets") { Span({ classes("font-bold", "text-primary") }) { Text(project.targets.size.toString()) } }
            MetadataRow("Packages") { Span({ classes("font-bold", "text-primary") }) { Text(project.totalPackages.toString()) } }
        }
        P({ classes("mt-5", "border-t", "border-line", "pt-4", "text-xs", "leading-5", "text-subtle") }) {
            Text("GitHub stars, license and activity dates are shown on klibs.io but are not part of the MCP searchProjects response.")
        }
    }
}

@Composable
private fun MetadataRow(
    label: String,
    value: @Composable () -> Unit,
) {
    Div({ classes("flex", "items-baseline", "justify-between", "gap-3") }) {
        Span({ classes("text-subtle") }) { Text(label) }
        value()
    }
}

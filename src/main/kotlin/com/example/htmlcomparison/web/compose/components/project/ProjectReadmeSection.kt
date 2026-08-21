package com.example.htmlcomparison.web.compose.components.project

import androidx.compose.runtime.Composable
import com.example.htmlcomparison.catalog.ProjectPage
import org.jetbrains.compose.web.attributes.ARel
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.rel
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Text

@Composable
internal fun ProjectReadmeSection(projectPage: ProjectPage) {
    Section({ classes("rounded-3xl", "border", "border-line", "bg-card", "p-6", "sm:p-8") }) {
        val readme = projectPage.readme
        if (readme == null) {
            P({ classes("text-sm", "leading-6", "text-muted") }) {
                Text(projectPage.readmeNotice ?: "No README is available for this project.")
            }
            return@Section
        }

        Div({ classes("readme") }) { RawHtml(readme.html) }
        P({ classes("mt-8", "border-t", "border-line", "pt-4", "text-xs", "text-subtle") }) {
            Text("Rendered from ")
            A(
                href = readme.sourceUrl,
                attrs = {
                    classes("font-bold", "text-accent", "no-underline", "transition", "hover:text-primary")
                    target(ATarget.Blank)
                    rel(ARel.NoReferrer)
                },
            ) {
                Text(readme.fileName)
            }
            Text(" on GitHub.")
        }
    }
}

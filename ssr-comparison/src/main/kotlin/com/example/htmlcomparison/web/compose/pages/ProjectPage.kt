package com.example.htmlcomparison.web.compose.pages

import androidx.compose.runtime.Composable
import com.example.htmlcomparison.catalog.ProjectPage
import com.example.htmlcomparison.catalog.ProjectTab
import com.example.htmlcomparison.web.compose.components.feedback.WarningBanner
import com.example.htmlcomparison.web.compose.components.layout.SiteFooter
import com.example.htmlcomparison.web.compose.components.layout.SiteHeader
import com.example.htmlcomparison.web.compose.components.project.MissingProject
import com.example.htmlcomparison.web.compose.components.project.ProjectBreadcrumb
import com.example.htmlcomparison.web.compose.components.project.ProjectDescription
import com.example.htmlcomparison.web.compose.components.project.ProjectMetadata
import com.example.htmlcomparison.web.compose.components.project.ProjectPackages
import com.example.htmlcomparison.web.compose.components.project.ProjectPlatforms
import com.example.htmlcomparison.web.compose.components.project.ProjectReadmeSection
import com.example.htmlcomparison.web.compose.components.project.ProjectTabs
import com.example.htmlcomparison.web.compose.components.project.ProjectTargets
import com.example.htmlcomparison.web.compose.components.project.ProjectTitle
import org.jetbrains.compose.web.dom.Aside
import org.jetbrains.compose.web.dom.Body
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Html
import org.jetbrains.compose.web.dom.Main

@Composable
internal fun ProjectPageDocument(
    projectPage: ProjectPage,
    formAction: String,
    otherRendererUrl: String,
) {
    Html(attrs = { lang("en") }) {
        DocumentHead(projectPage.name)
        Body(attrs = {
            classes("min-h-screen", "bg-canvas", "font-sans", "text-primary", "antialiased")
            attr("data-renderer", "compose-html")
        }) {
            PageBackdrop()
            SiteHeader(
                homeUrl = formAction,
                otherRendererUrl = otherRendererUrl,
                currentRenderer = "Compose HTML",
            )
            Main({ classes("mx-auto", "max-w-6xl", "px-6", "py-14", "sm:py-20") }) {
                ProjectBreadcrumb(projectPage, formAction)
                ProjectTitle(projectPage.name)
                WarningBanner(projectPage.warning)

                val project = projectPage.project
                if (project == null) {
                    MissingProject(projectPage, formAction)
                } else {
                    Div({ classes("mt-8", "grid", "gap-6", "lg:grid-cols-3", "lg:items-start") }) {
                        Div({ classes("space-y-6", "lg:col-span-2") }) {
                            ProjectDescription(project)
                            ProjectTabs(projectPage, formAction)
                            when (projectPage.tab) {
                                ProjectTab.README -> ProjectReadmeSection(projectPage)
                                ProjectTab.PACKAGES -> ProjectPackages(project)
                            }
                        }
                        Aside({ classes("space-y-6") }) {
                            ProjectPlatforms(project)
                            ProjectMetadata(project)
                            ProjectTargets(project)
                        }
                    }
                }
            }
            SiteFooter()
        }
    }
}

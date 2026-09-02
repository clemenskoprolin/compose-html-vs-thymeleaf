package com.example.htmlcomparison.web.compose.components.project

import androidx.compose.runtime.Composable
import com.example.htmlcomparison.catalog.ProjectCard
import com.example.htmlcomparison.catalog.ProjectPage
import com.example.htmlcomparison.catalog.ProjectTab
import org.jetbrains.compose.web.attributes.ARel
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.rel
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Nav
import org.jetbrains.compose.web.dom.Ol
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
internal fun ProjectBreadcrumb(
    projectPage: ProjectPage,
    formAction: String,
) {
    Nav({ attr("aria-label", "Breadcrumb") }) {
        Ol({ classes("flex", "flex-wrap", "items-center", "gap-2", "text-xs", "font-bold", "uppercase", "tracking-[0.16em]", "text-subtle") }) {
            Li {
                A(
                    href = projectPage.backUrl(formAction),
                    attrs = { classes("text-accent", "no-underline", "transition", "hover:text-primary") },
                ) {
                    Text("Catalog")
                }
            }
            Crumb(projectPage.author)
            Crumb(projectPage.name, current = true)
        }
    }
}

@Composable
private fun Crumb(
    label: String,
    current: Boolean = false,
) {
    Li({ classes("flex", "items-center", "gap-2") }) {
        Span({ attr("aria-hidden", "true") }) { Text("/") }
        if (current) {
            Span({
                classes("text-primary")
                attr("aria-current", "page")
            }) {
                Text(label)
            }
        } else {
            Span { Text(label) }
        }
    }
}

@Composable
internal fun ProjectTitle(name: String) {
    H1({ classes("mt-4", "break-words", "text-4xl", "font-black", "leading-tight", "tracking-tight", "text-primary", "sm:text-5xl", "") }) {
        Text(name)
    }
}

/** The description card klibs.io puts above the tabs. */
@Composable
internal fun ProjectDescription(project: ProjectCard) {
    Section({ classes("rounded-3xl", "border", "border-line", "bg-card", "p-6") }) {
        P({ classes("text-base", "leading-7", "text-muted") }) { Text(project.description) }
        Div({ classes("mt-6", "flex", "flex-wrap", "gap-3") }) {
            ProjectAction(project.url, "View on klibs.io", primary = true)
            ProjectAction(project.repositoryUrl, "GitHub repository", primary = false)
            ProjectAction(project.suggestEditUrl, "Suggest an edit", primary = false)
        }
    }
}

@Composable
private fun ProjectAction(
    href: String,
    label: String,
    primary: Boolean,
) {
    A(
        href = href,
        attrs = {
            classes("inline-flex", "items-center", "gap-2", "rounded-full", "border", if (primary) "border-accent" else "border-line-strong", if (primary) "bg-accent-solid" else "bg-surface", "px-5", "py-2.5", "text-sm", "font-bold", if (primary) "text-accent-ink" else "text-primary", "no-underline", "transition", "hover:-translate-y-0.5")
            target(ATarget.Blank)
            rel(ARel.NoReferrer)
        },
    ) {
        Text(label)
        Span({ attr("aria-hidden", "true") }) { Text("↗") }
    }
}

@Composable
internal fun ProjectTabs(
    projectPage: ProjectPage,
    formAction: String,
) {
    Nav({
        classes("flex", "gap-1", "border-b", "border-line")
        attr("aria-label", "Project sections")
    }) {
        projectPage.tabs.forEach { tab -> TabLink(projectPage, formAction, tab) }
    }
}

@Composable
private fun TabLink(
    projectPage: ProjectPage,
    formAction: String,
    tab: ProjectTab,
) {
    val selected = projectPage.isSelected(tab)
    A(
        href = projectPage.tabUrl(formAction, tab),
        attrs = {
            classes("-mb-px", "border-b-2", "px-4", "py-3", "text-sm", "font-bold", "no-underline", "transition")
            if (selected) {
                classes("border-accent", "text-primary")
            } else {
                classes("border-transparent", "text-subtle", "hover:text-primary")
            }
            if (selected) attr("aria-current", "page")
        },
    ) {
        Text(tab.label)
    }
}

@Composable
internal fun MissingProject(
    projectPage: ProjectPage,
    formAction: String,
) {
    Section({ classes("mt-8", "rounded-3xl", "border", "border-dashed", "border-line-strong", "px-6", "py-14", "text-center") }) {
        P({ classes("text-2xl", "font-black", "text-primary") }) { Text("Nothing to show for this project") }
        P({ classes("mx-auto", "mt-3", "max-w-md", "text-sm", "leading-6", "text-muted") }) {
            Text("The klibs.io MCP server did not return ${projectPage.author}/${projectPage.name}. It may have been renamed, or the search index may not list it.")
        }
        Div({ classes("mt-7", "flex", "flex-wrap", "justify-center", "gap-3") }) {
            A(
                href = projectPage.backUrl(formAction),
                attrs = { classes("inline-flex", "items-center", "gap-2", "rounded-full", "border", "border-accent", "bg-accent-solid", "px-5", "py-2.5", "text-sm", "font-bold", "text-accent-ink", "no-underline", "transition", "hover:-translate-y-0.5") },
            ) {
                Text("Back to the catalog")
            }
            ProjectAction(projectPage.klibsUrl, "Try klibs.io", primary = false)
        }
    }
}

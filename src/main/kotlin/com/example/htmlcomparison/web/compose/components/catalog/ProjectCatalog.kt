package com.example.htmlcomparison.web.compose.components.catalog

import androidx.compose.runtime.Composable
import com.example.htmlcomparison.catalog.CatalogPage
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Text

@Composable
internal fun ProjectCatalog(
    page: CatalogPage,
    formAction: String,
) {
    Section({ classes("mt-14") }) {
        CatalogHeading(page.status)
        if (page.projects.isEmpty()) {
            EmptyCatalog()
        } else {
            ProjectGrid(page, formAction)
        }
    }
}

@Composable
private fun CatalogHeading(status: String) {
    Div({ classes("mb-6", "flex", "flex-col", "gap-2", "sm:flex-row", "sm:items-end", "sm:justify-between") }) {
        Div {
            P({ classes("text-xs", "font-bold", "uppercase", "tracking-[0.2em]", "text-secondary") }) { Text("Catalog") }
            H2({ classes("mt-2", "text-2xl", "font-black", "text-primary") }) { Text("Projects worth opening") }
        }
        P({ classes("text-sm", "text-subtle") }) { Text(status) }
    }
}

@Composable
private fun EmptyCatalog() {
    Div({ classes("rounded-3xl", "border", "border-dashed", "border-line-strong", "px-6", "py-14", "text-center", "text-subtle") }) {
        Text("Try a broader search term.")
    }
}

@Composable
private fun ProjectGrid(
    page: CatalogPage,
    formAction: String,
) {
    Div({ classes("grid", "gap-4", "sm:grid-cols-2", "lg:grid-cols-3") }) {
        page.projects.forEach { project ->
            ProjectResultCard(project, page.projectUrl(formAction, project.author, project.name))
        }
    }
}

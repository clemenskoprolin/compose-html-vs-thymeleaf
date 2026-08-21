package com.example.htmlcomparison.web.compose.components.featured

import androidx.compose.runtime.Composable
import com.example.htmlcomparison.catalog.ProjectCategory
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
internal fun FeaturedCatalog(categories: List<ProjectCategory>) {
    Div {
        categories.forEachIndexed { index, category ->
            CategorySection(category)
            if (index == 0) {
                GrantBanner()
            }
        }
    }
}

@Composable
private fun CategorySection(category: ProjectCategory) {
    Section({
        classes("mt-14", "sm:mt-20")
        attr("data-category", category.slug)
    }) {
        Div({ classes("mb-6", "flex", "items-end", "justify-between", "gap-4") }) {
            H2({ classes("text-2xl", "font-black", "tracking-tight", "text-primary") }) { Text(category.title) }
            A(
                href = category.url,
                attrs = { classes("inline-flex", "shrink-0", "items-center", "gap-2", "text-sm", "font-bold", "text-accent", "no-underline", "hover:text-primary") },
            ) {
                Text("See all")
                Span({ attr("aria-hidden", "true") }) { Text("\u2192") }
            }
        }
        Div({ classes("grid", "gap-4", "sm:grid-cols-2", "lg:grid-cols-3") }) {
            category.projects.forEach { project -> RankedProjectCard(project) }
        }
    }
}

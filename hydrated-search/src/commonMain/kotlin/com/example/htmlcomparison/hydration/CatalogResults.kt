package com.example.htmlcomparison.hydration

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
internal fun CatalogResults(
    state: SearchState,
    isLoading: Boolean,
    onSearch: (SearchParams) -> Unit,
) {
    Div({
        id("catalog-results")
        attr("aria-live", "polite")
        if (isLoading) attr("aria-busy", "true")
    }) {
        TopTagRail(state, onSearch)
        WarningBanner(state.warning)
        if (state.isFeatured) {
            FeaturedCatalog(state)
        } else {
            ProjectCatalog(state)
        }
    }
}

@Composable
private fun TopTagRail(state: SearchState, onSearch: (SearchParams) -> Unit) {
    if (state.topTags.isEmpty()) return

    Div({
        classes("mt-6", "flex", "flex-wrap", "items-center", "gap-2")
        attr("aria-label", "Top tags")
    }) {
        P({ classes("mr-1", "text-xs", "font-bold", "uppercase", "tracking-[0.16em]", "text-subtle") }) {
            Text("Top tags")
        }
        TagChip(state, tag = "", label = "All", onSearch = onSearch)
        state.topTags.forEach { tag -> TagChip(state, tag, tag, onSearch) }
    }
}

@Composable
private fun TagChip(
    state: SearchState,
    tag: String,
    label: String,
    onSearch: (SearchParams) -> Unit,
) {
    val params = state.params(query = tag)
    val selected = state.query == tag
    A(
        href = params.url("/"),
        attrs = {
            classes("inline-flex", "items-center", "rounded-full", "border", "px-3", "py-1.5", "text-xs", "font-semibold", "no-underline", "transition")
            if (selected) {
                classes("border-accent-line", "bg-accent-solid", "text-accent-ink")
                attr("aria-current", "page")
            } else {
                classes("border-line-strong", "bg-surface", "text-muted", "hover:border-accent", "hover:text-primary")
            }
            onClick { event ->
                event.preventDefault()
                onSearch(params)
            }
        },
    ) { Text(label) }
}

@Composable
private fun WarningBanner(message: String?) {
    if (message == null) return

    Div({
        classes("mt-8", "rounded-2xl", "border", "border-warning-line", "bg-warning-soft", "px-5", "py-4", "text-sm", "text-warning")
        attr("role", "status")
    }) { Text(message) }
}

@Composable
private fun ProjectCatalog(state: SearchState) {
    Section({ classes("mt-14") }) {
        Div({ classes("mb-6", "flex", "flex-col", "gap-2", "sm:flex-row", "sm:items-end", "sm:justify-between") }) {
            Div {
                P({ classes("text-xs", "font-bold", "uppercase", "tracking-[0.2em]", "text-secondary") }) { Text("Catalog") }
                H2({ classes("mt-2", "text-2xl", "font-black", "text-primary") }) { Text("Projects worth opening") }
            }
            P({ classes("text-sm", "text-subtle") }) { Text(state.status) }
        }
        if (state.projects.isEmpty()) {
            Div({ classes("rounded-3xl", "border", "border-dashed", "border-line-strong", "px-6", "py-14", "text-center", "text-subtle") }) {
                Text("Try a broader search term.")
            }
        } else {
            Div({ classes("grid", "gap-4", "sm:grid-cols-2", "lg:grid-cols-3") }) {
                state.projects.forEach { project -> ProjectResultCard(project) }
            }
        }
    }
}

@Composable
private fun ProjectResultCard(project: ProjectSnapshot) {
    A(
        href = project.url,
        attrs = {
            classes("group", "flex", "h-full", "min-w-0", "flex-col", "justify-between", "gap-5", "rounded-2xl", "border", "border-line", "bg-card", "p-4", "no-underline", "transition", "hover:-translate-y-0.5", "hover:border-accent-line", "hover:bg-card-hover")
        },
    ) {
        Div {
            Div({ classes("min-w-0") }) {
                P({ classes("text-xs", "font-semibold", "text-subtle") }) { Text("by ${project.author}") }
                H3({ classes("mt-2", "break-words", "text-lg", "font-black", "text-primary") }) { Text(project.name) }
            }
            if (project.description.isNotBlank()) {
                P({ classes("project-card-description", "mt-3", "text-sm", "leading-6", "text-muted") }) {
                    Text(project.description)
                }
            }
            PlatformBadges(project.displayedPlatforms)
        }
        P({ classes("flex", "items-center", "justify-between", "text-xs", "font-bold", "text-accent") }) {
            Text("Open on klibs.io")
            Span({ attr("aria-hidden", "true") }) { Text("↗") }
        }
    }
}

@Composable
private fun FeaturedCatalog(state: SearchState) {
    Div {
        state.categories.forEachIndexed { index, category ->
            CategorySection(category)
            if (index == 0) GrantBanner()
        }
    }
}

@Composable
private fun CategorySection(category: CategorySnapshot) {
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
                Span({ attr("aria-hidden", "true") }) { Text("→") }
            }
        }
        Div({ classes("grid", "gap-4", "sm:grid-cols-2", "lg:grid-cols-3") }) {
            category.projects.forEach { project -> RankedProjectCard(project) }
        }
    }
}

@Composable
private fun RankedProjectCard(project: RankedProjectSnapshot) {
    A(
        href = project.url,
        attrs = {
            classes("group", "flex", "h-full", "min-w-0", "flex-col", "justify-between", "gap-5", "rounded-2xl", "border", if (project.grantWinner) "border-accent-line" else "border-line", "bg-card", "p-4", "no-underline", "transition", "hover:-translate-y-0.5", "hover:border-accent-line", "hover:bg-card-hover")
            attr("data-star-count", project.stars)
        },
    ) {
        Div {
            Div({ classes("flex", "items-start", "justify-between", "gap-4") }) {
                Div({ classes("min-w-0") }) {
                    P({ classes("text-xs", "font-semibold", "text-subtle") }) { Text("by ${project.author}") }
                    H3({ classes("mt-2", "break-words", "text-lg", "font-black", "text-primary") }) { Text(project.name) }
                }
                P({ classes("inline-flex", "shrink-0", "items-center", "gap-1", "rounded-full", "bg-code", "px-2.5", "py-1", "text-xs", "font-bold", "text-muted") }) {
                    Span({
                        classes("text-accent")
                        attr("aria-hidden", "true")
                    }) { Text("★") }
                    Text(project.stars)
                }
            }
            if (project.description.isNotBlank()) {
                P({ classes("project-card-description", "mt-3", "text-sm", "leading-6", "text-muted") }) { Text(project.description) }
            }
            if (project.grantWinner) {
                P({ classes("mt-3", "inline-flex", "rounded-full", "bg-secondary-soft", "px-2.5", "py-1", "text-[11px]", "font-bold", "uppercase", "tracking-wide", "text-secondary") }) {
                    Text("Kotlin Grant winner")
                }
            }
            ProjectTags(project.tags)
            PlatformBadges(project.platforms.take(5))
        }
        P({ classes("flex", "items-center", "justify-between", "text-xs", "font-bold", "text-accent") }) {
            Text("Open on klibs.io")
            Span({ attr("aria-hidden", "true") }) { Text("↗") }
        }
    }
}

@Composable
private fun ProjectTags(tags: List<String>) {
    if (tags.isEmpty()) return
    val displayed = tags.take(4)

    Div({ classes("mt-3", "flex", "flex-wrap", "gap-1.5") }) {
        displayed.forEach { tag ->
            Span({ classes("rounded-full", "bg-secondary-soft", "px-2.5", "py-1", "text-[11px]", "font-bold", "text-secondary") }) { Text(tag) }
        }
        val additionalCount = tags.size - displayed.size
        if (additionalCount > 0) {
            Span({ classes("px-1", "py-1", "text-[11px]", "font-bold", "text-subtle") }) { Text("+$additionalCount") }
        }
    }
}

@Composable
private fun PlatformBadges(platforms: List<String>) {
    if (platforms.isEmpty()) return

    Div({ classes("mt-4") }) {
        Div({ classes("flex", "flex-wrap", "gap-1.5") }) {
            platforms.forEach { platform ->
                Span({ classes("rounded-md", "border", "border-line", "bg-surface", "px-2", "py-1", "text-[10px]", "font-semibold", "text-muted") }) { Text(platform) }
            }
        }
    }
}

@Composable
private fun GrantBanner() {
    Section({ classes("grant-banner", "mt-14", "overflow-hidden", "rounded-3xl", "border", "border-accent-line", "p-7", "sm:mt-20") }) {
        Div({ classes("relative", "z-10", "max-w-2xl", "lg:max-w-lg") }) {
            P({ classes("mb-5", "text-xs", "font-bold", "uppercase", "tracking-[0.2em]", "text-accent") }) { Text("Kotlin Foundation") }
            H2({ classes("text-4xl", "font-black", "leading-tight", "tracking-tight", "text-primary") }) { Text("Kotlin Grant Winners") }
            P({ classes("mt-4", "text-base", "leading-6", "text-muted") }) {
                Text("Explore top-tier projects funded for pushing Multiplatform development forward.")
            }
            Div({ classes("mt-8", "flex", "flex-wrap", "gap-3") }) {
                GrantLink("https://klibs.io/?category=grant-winners", "Discover projects", primary = true)
                GrantLink("https://kotlinfoundation.org/grants/", "Learn about grants", primary = false)
            }
        }
        Img(src = "/assets/kodee-grant-winner.svg", alt = "Kodee holding a Kotlin grant winner trophy") {
            classes("grant-figure")
            attr("width", "490")
            attr("height", "235")
        }
    }
}

@Composable
private fun GrantLink(href: String, label: String, primary: Boolean) {
    A(
        href = href,
        attrs = {
            classes("inline-flex", "items-center", "gap-2", "rounded-full", "border", if (primary) "border-accent" else "border-line-strong", if (primary) "bg-accent-solid" else "bg-surface", "px-5", "py-2.5", "text-sm", "font-bold", if (primary) "text-accent-ink" else "text-primary", "no-underline", "transition", "hover:-translate-y-0.5")
        },
    ) {
        Text(label)
        Span({ attr("aria-hidden", "true") }) { Text("→") }
    }
}

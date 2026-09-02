package com.example.htmlcomparison.hydration

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.attributes.AutoComplete
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.FormMethod
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.autoComplete
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.method
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.attributes.onSubmit
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Form
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Main
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

const val SearchApiPath = "/api/search"
internal const val SearchInputId = "catalog-query"
private const val PagePath = "/"

/**
 * The UI shared by the server renderer and the browser.
 *
 * The server passes [state.query] as [draftQuery] and no-op callbacks. The browser passes its live input
 * state and real callbacks. Keeping both renders on this one composable is what lets hydration
 * adopt the server DOM instead of replacing it.
 */
@Composable
fun SearchView(
    state: SearchState,
    draftQuery: String,
    isLoading: Boolean,
    onQueryChange: (String) -> Unit,
    onSearch: (SearchParams) -> Unit,
) {
    Main({ classes("mx-auto", "max-w-6xl", "px-6", "py-14", "sm:py-20") }) {
        SearchHero(state, draftQuery, isLoading, onQueryChange, onSearch)
        CatalogResults(state, isLoading, onSearch)
    }
}

@Composable
private fun SearchHero(
    state: SearchState,
    draftQuery: String,
    isLoading: Boolean,
    onQueryChange: (String) -> Unit,
    onSearch: (SearchParams) -> Unit,
) {
    Section({ classes("max-w-3xl") }) {
        P({ classes("mb-5", "text-sm", "font-bold", "uppercase", "tracking-[0.22em]", "text-accent") }) {
            Text("Kotlin Multiplatform, indexed")
        }
        H1({ classes("text-4xl", "font-black", "leading-tight", "tracking-tight", "text-primary", "sm:text-6xl") }) {
            Text("Find the library that gets you to the interesting part.")
        }
        P({ classes("mt-6", "max-w-2xl", "text-lg", "leading-8", "text-muted") }) {
            Text("The server sends useful HTML and its public state; Compose adopts it and makes search interactive.")
        }
        SearchForm(state, draftQuery, isLoading, onQueryChange, onSearch)
    }
}

@Composable
private fun SearchForm(
    state: SearchState,
    draftQuery: String,
    isLoading: Boolean,
    onQueryChange: (String) -> Unit,
    onSearch: (SearchParams) -> Unit,
) {
    Form(
        action = PagePath,
        attrs = {
            classes("mt-9")
            method(FormMethod.Get)
            onSubmit { event ->
                event.preventDefault()
                onSearch(state.params(query = draftQuery))
            }
        },
    ) {
        Div({ classes("flex", "flex-col", "gap-3", "rounded-2xl", "border", "border-line", "bg-surface-raised", "p-3", "shadow-2xl", "shadow-shadow", "sm:flex-row") }) {
            Input(type = InputType.Search) {
                id(SearchInputId)
                classes("min-w-0", "flex-1", "rounded-xl", "border", "border-line", "bg-input", "px-5", "py-3.5", "text-base", "text-primary", "outline-none", "placeholder:text-subtle", "focus:border-accent")
                // The attribute is SSR-visible; the property keeps the hydrated input controlled.
                defaultValue(draftQuery)
                value(draftQuery)
                onInput { onQueryChange(it.value) }
                name("query")
                placeholder("Try html, navigation, database…")
                attr("aria-label", "Search Kotlin Multiplatform projects")
                autoComplete(AutoComplete.off)
            }
            Button(attrs = {
                classes("rounded-xl", "bg-accent-solid", "px-7", "py-3.5", "text-sm", "font-black", "text-accent-ink", "transition", "hover:bg-accent-hover")
                type(ButtonType.Submit)
                if (isLoading) disabled()
            }) {
                Text(if (isLoading) "Searching…" else "Search projects")
            }
        }
        state.selectedPlatformIds.forEach { platform ->
            Input(type = InputType.Hidden) {
                name("platforms")
                value(platform)
            }
        }
        PlatformFilter(state, onSearch)
    }
}

@Composable
private fun PlatformFilter(state: SearchState, onSearch: (SearchParams) -> Unit) {
    Div({
        classes("mt-5", "flex", "flex-wrap", "items-center", "gap-2")
        attr("role", "group")
        attr("aria-label", "Platform filter")
    }) {
        P({ classes("mr-1", "text-xs", "font-bold", "uppercase", "tracking-[0.16em]", "text-subtle") }) {
            Text("Platforms")
        }
        state.platforms.forEach { platform -> PlatformChip(state, platform, onSearch) }
    }
}

@Composable
private fun PlatformChip(
    state: SearchState,
    platform: PlatformSnapshot,
    onSearch: (SearchParams) -> Unit,
) {
    val selectedIds = state.selectedPlatformIds
    val nextIds = if (platform.selected) selectedIds - platform.id else selectedIds + platform.id
    val params = state.params(platforms = nextIds)
    A(
        href = params.url(PagePath),
        attrs = {
            classes("inline-flex", "items-center", "gap-1", "rounded-full", "border", "px-3", "py-1.5", "text-xs", "font-semibold", "no-underline", "transition")
            if (platform.selected) {
                classes("border-accent-line", "bg-accent-solid", "text-accent-ink")
            } else {
                classes("border-line-strong", "bg-surface", "text-muted", "hover:border-accent", "hover:text-primary")
            }
            attr(
                "aria-label",
                "${platform.label} platform, ${if (platform.selected) "selected; activate to remove" else "activate to add"}",
            )
            onClick { event ->
                event.preventDefault()
                onSearch(params)
            }
        },
    ) {
        if (platform.selected) {
            Span({ attr("aria-hidden", "true") }) { Text("✓") }
        }
        Text(platform.label)
    }
}

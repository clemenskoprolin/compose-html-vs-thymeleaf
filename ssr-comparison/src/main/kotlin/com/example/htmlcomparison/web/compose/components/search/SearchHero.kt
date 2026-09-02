package com.example.htmlcomparison.web.compose.components.search

import androidx.compose.runtime.Composable
import com.example.htmlcomparison.catalog.CatalogPage
import com.example.htmlcomparison.catalog.Platform
import org.jetbrains.compose.web.attributes.AutoComplete
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.FormMethod
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.autoComplete
import org.jetbrains.compose.web.attributes.method
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Form
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
internal fun SearchHero(
    page: CatalogPage,
    formAction: String,
) {
    Section({ classes("max-w-3xl") }) {
        P({ classes("mb-5", "text-sm", "font-bold", "uppercase", "tracking-[0.22em]", "text-accent") }) {
            Text("Kotlin Multiplatform, indexed")
        }
        H1({ classes("text-4xl", "font-black", "leading-tight", "tracking-tight", "text-primary", "sm:text-6xl") }) {
            Text("Find the library that gets you to the interesting part.")
        }
        P({ classes("mt-6", "max-w-2xl", "text-lg", "leading-8", "text-muted") }) {
            Text("Search verified project and package metadata from klibs.io through its public MCP server.")
        }
        SearchForm(page, formAction)
    }
}

@Composable
private fun SearchForm(
    page: CatalogPage,
    formAction: String,
) {
    // Both renderers submit the same ordinary GET request and return a complete HTML page.
    Form(
        action = formAction,
        attrs = {
            classes("mt-9")
            method(FormMethod.Get)
        },
    ) {
        Div({ classes("flex", "flex-col", "gap-3", "rounded-2xl", "border", "border-line", "bg-surface-raised", "p-3", "shadow-2xl", "shadow-shadow", "sm:flex-row") }) {
            Input(type = InputType.Search) {
                classes("min-w-0", "flex-1", "rounded-xl", "border", "border-line", "bg-input", "px-5", "py-3.5", "text-base", "text-primary", "outline-none", "placeholder:text-subtle", "focus:border-accent")
                // Controlled input values are DOM properties. For string rendering,
                // emit the initial value as an HTML attribute instead.
                defaultValue(page.query)
                name("query")
                placeholder("Try html, navigation, database…")
                attr("aria-label", "Search Kotlin Multiplatform projects")
                autoComplete(AutoComplete.off)
            }
            Button(attrs = {
                classes("rounded-xl", "bg-accent-solid", "px-7", "py-3.5", "text-sm", "font-black", "text-accent-ink", "transition", "hover:bg-accent-hover")
                type(ButtonType.Submit)
            }) {
                Text("Search projects")
            }
        }
        page.platforms.forEach { platform ->
            Input(type = InputType.Hidden) {
                name("platforms")
                value(platform)
            }
        }
        PlatformFilter(page, formAction)
    }
}

@Composable
private fun PlatformFilter(
    page: CatalogPage,
    formAction: String,
) {
    Div({
        classes("mt-5", "flex", "flex-wrap", "items-center", "gap-2")
        attr("role", "group")
        attr("aria-label", "Platform filter")
    }) {
        P({ classes("mr-1", "text-xs", "font-bold", "uppercase", "tracking-[0.16em]", "text-subtle") }) {
            Text("Platforms")
        }
        Platform.Filters.forEach { platform -> PlatformChip(page, formAction, platform) }
    }
}

@Composable
private fun PlatformChip(
    page: CatalogPage,
    formAction: String,
    platform: Platform,
) {
    val selected = page.isPlatformSelected(platform)
    A(
        href = page.platformUrl(formAction, platform),
        attrs = {
            classes("inline-flex", "items-center", "gap-1", "rounded-full", "border", "px-3", "py-1.5", "text-xs", "font-semibold", "no-underline", "transition")
            if (selected) {
                classes("border-accent-line", "bg-accent-solid", "text-accent-ink")
            } else {
                classes("border-line-strong", "bg-surface", "text-muted", "hover:border-accent", "hover:text-primary")
            }
            attr(
                "aria-label",
                "${platform.label} platform, ${if (selected) "selected; activate to remove" else "activate to add"}",
            )
        },
    ) {
        if (selected) {
            Span({ attr("aria-hidden", "true") }) { Text("✓") }
        }
        Text(platform.label)
    }
}

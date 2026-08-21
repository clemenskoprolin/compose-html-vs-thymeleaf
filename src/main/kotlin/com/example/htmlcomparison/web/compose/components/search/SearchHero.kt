package com.example.htmlcomparison.web.compose.components.search

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.FormMethod
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.method
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Form
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Text

@Composable
internal fun SearchHero(
    query: String,
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
        SearchForm(query, formAction)
    }
}

@Composable
private fun SearchForm(
    query: String,
    formAction: String,
) {
    Form(
        action = formAction,
        attrs = {
            classes("mt-9", "flex", "flex-col", "gap-3", "rounded-2xl", "border", "border-line", "bg-surface-raised", "p-3", "shadow-2xl", "shadow-shadow", "sm:flex-row")
            method(FormMethod.Get)
        },
    ) {
        Input(type = InputType.Search) {
            classes("min-w-0", "flex-1", "rounded-xl", "border", "border-line", "bg-input", "px-5", "py-3.5", "text-base", "text-primary", "outline-none", "placeholder:text-subtle", "focus:border-accent")
            // Controlled input values are DOM properties. For string rendering,
            // emit the initial value as an HTML attribute instead.
            defaultValue(query)
            name("query")
            placeholder("Try html, navigation, database\u2026")
            attr("aria-label", "Search Kotlin Multiplatform projects")
        }
        Button(attrs = {
            classes("rounded-xl", "bg-accent-solid", "px-7", "py-3.5", "text-sm", "font-black", "text-accent-ink", "transition", "hover:bg-accent-hover")
            type(ButtonType.Submit)
        }) {
            Text("Search projects")
        }
    }
}

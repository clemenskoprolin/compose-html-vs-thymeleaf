package com.example.htmlcomparison.hydration

import androidx.compose.runtime.Composable
import kotlinx.browser.dom.HTMLElement
import org.jetbrains.compose.web.HydrationRoot
import org.jetbrains.compose.web.attributes.LinkRel
import org.jetbrains.compose.web.attributes.ScriptType
import org.jetbrains.compose.web.attributes.href
import org.jetbrains.compose.web.attributes.rel
import org.jetbrains.compose.web.attributes.src
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.dom.Body
import org.jetbrains.compose.web.dom.Head
import org.jetbrains.compose.web.dom.Html
import org.jetbrains.compose.web.dom.Link
import org.jetbrains.compose.web.dom.Meta
import org.jetbrains.compose.web.dom.Script
import org.jetbrains.compose.web.dom.TagElement
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Title
import org.jetbrains.compose.web.renderHydratedDocument
import org.springframework.stereotype.Component

private const val SearchRootId = "search-page"

@Component
class SearchDocumentRenderer {
    fun render(state: SearchState, ssrComparisonUrl: String): String =
        renderHydratedDocument {
            SearchDocument(state, ssrComparisonUrl)
        }
}

/** The static document shell stays on the server; only [SearchView] is hydrated. */
@Composable
private fun SearchDocument(state: SearchState, ssrComparisonUrl: String) {
    Html(attrs = { lang("en") }) {
        Head {
            Meta { attr("charset", "UTF-8") }
            Meta {
                attr("name", "viewport")
                attr("content", "width=device-width, initial-scale=1")
            }
            Title { Text("KMP Library Finder — Hydrated Compose HTML") }
            Link(attrs = {
                rel(LinkRel.Stylesheet)
                href("/app.css")
            })
            ExternalScript(url = "/theme.js")
        }
        Body(attrs = {
            classes("min-h-screen", "bg-canvas", "font-sans", "text-primary", "antialiased")
            attr("data-renderer", "compose-html-hydrated")
        }) {
            PageBackdrop()
            SiteHeader(ssrComparisonUrl)
            NoScriptMessage()
            HydrationRoot(
                initialState = state,
                serializeState = SearchState::toJson,
                rootAttrs = { id(SearchRootId) },
            ) { initialState ->
                SearchView(
                    state = initialState,
                    draftQuery = initialState.query,
                    isLoading = false,
                    onQueryChange = {},
                    onSearch = {},
                )
            }
            Script(attrs = {
                type(ScriptType.Module)
                src("/search-client.js")
            })
            SiteFooter()
        }
    }
}

@Composable
private fun ExternalScript(url: String) {
    Script(attrs = { src(url) })
}

@Composable
private fun NoScriptMessage() {
    TagElement<HTMLElement>(
        tagName = "noscript",
        applyAttrs = null,
        content = { Text("Search still works as a normal GET request without JavaScript.") },
    )
}

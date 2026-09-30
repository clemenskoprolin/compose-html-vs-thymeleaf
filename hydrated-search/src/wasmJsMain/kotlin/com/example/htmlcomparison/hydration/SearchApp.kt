@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package com.example.htmlcomparison.hydration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.js.JsAny
import kotlin.js.JsString
import kotlin.js.Promise
import kotlin.time.Duration.Companion.milliseconds

private val SearchDebounce = 250.milliseconds

private external interface SearchResponse : JsAny {
    val ok: Boolean
    val status: Int
    fun text(): Promise<JsString>
}

@JsName("fetch")
private external fun fetchSearch(input: String): Promise<SearchResponse>

@JsFun("message => console.error(message)")
private external fun consoleError(message: String)

/** Owns all browser-only state; [SearchView] remains shared with the JVM renderer. */
@Composable
internal fun SearchApp(
    initialState: SearchState,
    loadSearchResults: suspend (SearchParams) -> SearchState = ::fetchSearchResults,
    updateUrl: (SearchState) -> Unit = ::replaceUrl,
) {
    var searchState by remember { mutableStateOf(initialState) }
    var draftQuery by remember { mutableStateOf(initialState.query) }
    var requestedQuery by remember { mutableStateOf(initialState.query) }
    var isLoading by remember { mutableStateOf(false) }
    var requestGeneration by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    fun search(params: SearchParams) {
        val generation = requestGeneration + 1
        requestGeneration = generation
        requestedQuery = params.query
        isLoading = true

        scope.launch {
            try {
                val nextState = loadSearchResults(params)
                if (generation == requestGeneration) {
                    searchState = nextState
                    draftQuery = nextState.query
                    requestedQuery = nextState.query
                    updateUrl(nextState)
                }
            } catch (failure: Throwable) {
                consoleError("Hydrated catalog search failed\n${failure.stackTraceToString()}")
                if (generation == requestGeneration) {
                    searchState = searchState.copy(
                        status = "The previous results are still shown.",
                        warning = "The search request could not be completed. Please try again.",
                    )
                }
            } finally {
                if (generation == requestGeneration) isLoading = false
            }
        }
    }

    // A new keystroke restarts this effect. Rechecking after the pause prevents a form submit from
    // being followed by a duplicate debounced request.
    LaunchedEffect(draftQuery) {
        if (draftQuery == requestedQuery) return@LaunchedEffect
        delay(SearchDebounce)
        if (draftQuery != requestedQuery) search(searchState.params(query = draftQuery))
    }

    SearchView(
        state = searchState,
        draftQuery = draftQuery,
        isLoading = isLoading,
        onQueryChange = { nextQuery ->
            draftQuery = nextQuery
            // Invalidate an older response immediately, before this draft reaches the debounce.
            requestGeneration++
            requestedQuery = searchState.query
            isLoading = false
        },
        onSearch = { params ->
            draftQuery = params.query
            search(params)
        },
    )
}

internal suspend fun fetchSearchResults(params: SearchParams): SearchState {
    val response = fetchSearch(params.url(SearchApiPath)).await<SearchResponse>()
    check(response.ok) { "Search returned HTTP ${response.status}" }
    return searchStateFromJson(response.text().await<JsString>().toString())
}

private fun replaceUrl(state: SearchState) {
    window.history.replaceState(null, "", state.params().url("/"))
}

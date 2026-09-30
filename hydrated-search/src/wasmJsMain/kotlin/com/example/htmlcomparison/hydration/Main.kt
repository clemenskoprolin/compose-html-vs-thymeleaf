@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package com.example.htmlcomparison.hydration

import org.jetbrains.compose.web.hydrateRoot

@JsFun("message => console.error(message)")
private external fun consoleError(message: String)

fun main() {
    hydrateRoot(
        deserializeState = ::searchStateFromJson,
        onHydrationMismatch = { mismatch ->
            consoleError("Compose hydration failed; falling back to client rendering.\n${mismatch.stackTraceToString()}")
        },
    ) { initialState ->
        SearchApp(initialState)
    }
}

package com.example.htmlcomparison.hydration

import org.jetbrains.compose.web.hydrateRoot
import kotlin.js.console

fun main() {
    hydrateRoot(
        deserializeState = ::searchStateFromJson,
        onHydrationMismatch = { mismatch ->
            console.error("Compose hydration failed; falling back to client rendering.", mismatch)
        },
    ) { initialState ->
        SearchApp(initialState)
    }
}

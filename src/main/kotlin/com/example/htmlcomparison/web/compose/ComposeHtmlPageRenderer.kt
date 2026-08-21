package com.example.htmlcomparison.web.compose

import com.example.htmlcomparison.catalog.CatalogPage
import com.example.htmlcomparison.web.compose.pages.CatalogPageDocument
import org.jetbrains.compose.web.composeHtmlToString
import org.springframework.stereotype.Component

@Component
class ComposeHtmlPageRenderer {
    fun render(
        page: CatalogPage,
        formAction: String,
        otherRendererUrl: String,
    ): String = "<!doctype html>" + composeHtmlToString {
        CatalogPageDocument(
            page = page,
            formAction = formAction,
            otherRendererUrl = otherRendererUrl,
        )
    }
}

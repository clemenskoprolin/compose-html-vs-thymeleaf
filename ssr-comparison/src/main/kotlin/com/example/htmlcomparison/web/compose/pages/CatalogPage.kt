package com.example.htmlcomparison.web.compose.pages

import androidx.compose.runtime.Composable
import com.example.htmlcomparison.catalog.CatalogPage
import com.example.htmlcomparison.web.compose.components.layout.SiteFooter
import com.example.htmlcomparison.web.compose.components.layout.SiteHeader
import com.example.htmlcomparison.web.compose.components.search.SearchHero
import org.jetbrains.compose.web.dom.Body
import org.jetbrains.compose.web.dom.Html
import org.jetbrains.compose.web.dom.Main

@Composable
internal fun CatalogPageDocument(
    page: CatalogPage,
    formAction: String,
    otherRendererUrl: String,
) {
    Html(attrs = { lang("en") }) {
        DocumentHead()
        Body(attrs = {
            classes("min-h-screen", "bg-canvas", "font-sans", "text-primary", "antialiased")
            attr("data-renderer", "compose-html")
        }) {
            PageBackdrop()
            SiteHeader(
                homeUrl = formAction,
                otherRendererUrl = otherRendererUrl,
                currentRenderer = "Compose HTML",
            )
            Main({ classes("mx-auto", "max-w-6xl", "px-6", "py-14", "sm:py-20") }) {
                SearchHero(page = page, formAction = formAction)
                CatalogResults(page = page, formAction = formAction)
            }
            SiteFooter()
        }
    }
}

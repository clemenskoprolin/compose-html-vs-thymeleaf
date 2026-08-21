package com.example.htmlcomparison.web.compose.pages

import androidx.compose.runtime.Composable
import com.example.htmlcomparison.catalog.CatalogPage
import com.example.htmlcomparison.web.compose.components.catalog.ProjectCatalog
import com.example.htmlcomparison.web.compose.components.feedback.WarningBanner
import com.example.htmlcomparison.web.compose.components.featured.FeaturedCatalog
import com.example.htmlcomparison.web.compose.components.layout.SiteFooter
import com.example.htmlcomparison.web.compose.components.layout.SiteHeader
import com.example.htmlcomparison.web.compose.components.search.SearchHero
import kotlinx.browser.dom.HTMLElement
import org.jetbrains.compose.web.attributes.LinkRel
import org.jetbrains.compose.web.attributes.href
import org.jetbrains.compose.web.attributes.rel
import org.jetbrains.compose.web.dom.Body
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Head
import org.jetbrains.compose.web.dom.Html
import org.jetbrains.compose.web.dom.Link
import org.jetbrains.compose.web.dom.Main
import org.jetbrains.compose.web.dom.Meta
import org.jetbrains.compose.web.dom.TagElement
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Title

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
            CatalogMain(page, formAction)
            SiteFooter()
        }
    }
}

@Composable
private fun DocumentHead() {
    Head {
        Meta { attr("charset", "UTF-8") }
        Meta {
            attr("name", "viewport")
            attr("content", "width=device-width, initial-scale=1")
        }
        Title { Text("KMP Library Finder") }
        Link {
            rel(LinkRel.Stylesheet)
            href("/app.css")
        }
        ExternalScript("/theme.js")
    }
}

@Composable
private fun PageBackdrop() {
    Div({ classes("page-glow", "fixed", "inset-0", "-z-10") })
}

@Composable
private fun CatalogMain(
    page: CatalogPage,
    formAction: String,
) {
    Main({ classes("mx-auto", "max-w-6xl", "px-6", "py-14", "sm:py-20") }) {
        SearchHero(query = page.query, formAction = formAction)
        WarningBanner(page.warning)
        if (page.isFeatured) {
            FeaturedCatalog(page.categories)
        } else {
            ProjectCatalog(projects = page.projects, status = page.status)
        }
    }
}

@Composable
private fun ExternalScript(src: String) {
    TagElement<HTMLElement>(
        tagName = "script",
        applyAttrs = { attr("src", src) },
        content = {},
    )
}

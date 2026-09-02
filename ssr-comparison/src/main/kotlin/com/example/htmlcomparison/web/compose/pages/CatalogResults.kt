package com.example.htmlcomparison.web.compose.pages

import androidx.compose.runtime.Composable
import com.example.htmlcomparison.catalog.CatalogPage
import com.example.htmlcomparison.web.compose.components.catalog.ProjectCatalog
import com.example.htmlcomparison.web.compose.components.featured.FeaturedCatalog
import com.example.htmlcomparison.web.compose.components.feedback.WarningBanner
import com.example.htmlcomparison.web.compose.components.search.TopTagRail
import org.jetbrains.compose.web.dom.Div

/** The catalog content produced as part of every complete server-rendered page. */
@Composable
internal fun CatalogResults(
    page: CatalogPage,
    formAction: String,
) {
    Div({ id("catalog-results") }) {
        TopTagRail(page, formAction)
        WarningBanner(page.warning)
        if (page.isFeatured) {
            FeaturedCatalog(page = page, formAction = formAction)
        } else {
            ProjectCatalog(page = page, formAction = formAction)
        }
    }
}

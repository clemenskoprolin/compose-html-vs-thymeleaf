package com.example.htmlcomparison.web.compose

import com.example.htmlcomparison.catalog.CatalogPage
import com.example.htmlcomparison.catalog.ProjectPage
import com.example.htmlcomparison.web.compose.pages.CatalogPageDocument
import com.example.htmlcomparison.web.compose.pages.ProjectPageDocument
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

    fun renderProject(
        projectPage: ProjectPage,
        formAction: String,
        otherRendererUrl: String,
    ): String = "<!doctype html>" + composeHtmlToString {
        ProjectPageDocument(
            projectPage = projectPage,
            formAction = formAction,
            otherRendererUrl = otherRendererUrl,
        )
    }
}

package com.example.htmlcomparison.web.thymeleaf

import com.example.htmlcomparison.catalog.CatalogPage
import com.example.htmlcomparison.catalog.ProjectPage
import org.springframework.stereotype.Component
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine

@Component
class ThymeleafPageRenderer(
    private val templateEngine: SpringTemplateEngine,
) {
    fun render(
        page: CatalogPage,
        formAction: String,
        otherRendererUrl: String,
    ): String = templateEngine.process(
        "catalog",
        context(
            "page" to page,
            "formAction" to formAction,
            "otherRendererUrl" to otherRendererUrl,
        ),
    )

    fun renderProject(
        projectPage: ProjectPage,
        formAction: String,
        otherRendererUrl: String,
    ): String = templateEngine.process(
        "project",
        context(
            "projectPage" to projectPage,
            "formAction" to formAction,
            "otherRendererUrl" to otherRendererUrl,
        ),
    )

    private fun context(vararg variables: Pair<String, Any>): Context = Context().apply {
        variables.forEach { (name, value) -> setVariable(name, value) }
    }
}

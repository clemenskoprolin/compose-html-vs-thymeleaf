package com.example.htmlcomparison.web

import com.example.htmlcomparison.catalog.CatalogPage
import com.example.htmlcomparison.catalog.CatalogService
import com.example.htmlcomparison.catalog.Platform
import com.example.htmlcomparison.web.compose.ComposeHtmlPageRenderer
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.util.UriComponentsBuilder

@Controller
class FrontendController(
    private val catalogService: CatalogService,
    private val composeHtmlPageRenderer: ComposeHtmlPageRenderer,
) {
    @GetMapping("/")
    fun index(): String = "redirect:$COMPOSE_HTML"

    @GetMapping(COMPOSE_HTML, produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun composeHtml(
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) platforms: List<String>?,
    ): ResponseEntity<String> {
        val page = catalogService.page(query, platforms)
        return html(
            composeHtmlPageRenderer.render(
                page = page,
                formAction = COMPOSE_HTML,
                otherRendererUrl = comparisonUrl(THYMELEAF, page),
            )
        )
    }

    @GetMapping("$COMPOSE_HTML/project/{author}/{name}", produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun composeHtmlProject(
        @PathVariable author: String,
        @PathVariable name: String,
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) platforms: List<String>?,
        @RequestParam(required = false) tab: String?,
    ): ResponseEntity<String> {
        val projectPage = catalogService.project(author, name, tab)
            .copy(backParameters = backContext(query, platforms).searchParameters)
        return html(
            composeHtmlPageRenderer.renderProject(
                projectPage = projectPage,
                formAction = COMPOSE_HTML,
                otherRendererUrl = projectPage.tabUrl(THYMELEAF, projectPage.tab),
            )
        )
    }

    @GetMapping(THYMELEAF)
    fun thymeleaf(
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) platforms: List<String>?,
        model: Model,
    ): String {
        val page = catalogService.page(query, platforms)
        model.addAttribute("page", page)
        model.addAttribute("formAction", THYMELEAF)
        model.addAttribute("otherRendererUrl", comparisonUrl(COMPOSE_HTML, page))
        return "catalog"
    }

    @GetMapping("$THYMELEAF/project/{author}/{name}")
    fun thymeleafProject(
        @PathVariable author: String,
        @PathVariable name: String,
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) platforms: List<String>?,
        @RequestParam(required = false) tab: String?,
        model: Model,
    ): String {
        val projectPage = catalogService.project(author, name, tab)
            .copy(backParameters = backContext(query, platforms).searchParameters)
        model.addAttribute("projectPage", projectPage)
        model.addAttribute("formAction", THYMELEAF)
        model.addAttribute("otherRendererUrl", projectPage.tabUrl(COMPOSE_HTML, projectPage.tab))
        return "project"
    }

    /**
     * The originating search, used only to rebuild links. It never runs a search of its own,
     * so opening a project costs a single MCP lookup.
     */
    private fun backContext(query: String?, platforms: List<String>?) = CatalogPage(
        query = query.orEmpty().trim(),
        platforms = Platform.select(platforms),
        projects = emptyList(),
        status = "",
    )

    private fun html(body: String): ResponseEntity<String> = ResponseEntity.ok()
        .contentType(MediaType.TEXT_HTML)
        .body(body)

    /** Carries the active search to the other renderer so switching keeps the page in place. */
    private fun comparisonUrl(path: String, page: CatalogPage): String {
        if (page.query.isBlank() && page.platforms.isEmpty()) return path
        val url = UriComponentsBuilder.fromPath(path)
            .queryParam("query", page.query)
        if (page.platforms.isNotEmpty()) url.queryParam("platforms", page.platforms)
        return url.build()
            .encode()
            .toUriString()
    }

    private companion object {
        const val COMPOSE_HTML = "/composehtml"
        const val THYMELEAF = "/thymeleaf"
    }
}

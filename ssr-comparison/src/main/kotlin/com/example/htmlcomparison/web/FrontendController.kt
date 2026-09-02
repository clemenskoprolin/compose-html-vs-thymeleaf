package com.example.htmlcomparison.web

import com.example.htmlcomparison.catalog.CatalogPage
import com.example.htmlcomparison.catalog.CatalogService
import com.example.htmlcomparison.catalog.Platform
import com.example.htmlcomparison.web.compose.ComposeHtmlPageRenderer
import com.example.htmlcomparison.web.thymeleaf.ThymeleafPageRenderer
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.util.UriComponentsBuilder
import kotlin.time.measureTimedValue

@Controller
class FrontendController(
    private val catalogService: CatalogService,
    private val composeHtmlPageRenderer: ComposeHtmlPageRenderer,
    private val thymeleafPageRenderer: ThymeleafPageRenderer,
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
            timedRender(renderer = "compose-html", page = "catalog") {
                composeHtmlPageRenderer.render(
                    page = page,
                    formAction = COMPOSE_HTML,
                    otherRendererUrl = comparisonUrl(THYMELEAF, page),
                )
            }
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
            timedRender(renderer = "compose-html", page = "project") {
                composeHtmlPageRenderer.renderProject(
                    projectPage = projectPage,
                    formAction = COMPOSE_HTML,
                    otherRendererUrl = projectPage.tabUrl(THYMELEAF, projectPage.tab),
                )
            }
        )
    }

    @GetMapping(THYMELEAF, produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun thymeleaf(
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) platforms: List<String>?,
    ): ResponseEntity<String> {
        val page = catalogService.page(query, platforms)
        return html(
            timedRender(renderer = "thymeleaf", page = "catalog") {
                thymeleafPageRenderer.render(
                    page = page,
                    formAction = THYMELEAF,
                    otherRendererUrl = comparisonUrl(COMPOSE_HTML, page),
                )
            }
        )
    }

    @GetMapping("$THYMELEAF/project/{author}/{name}", produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun thymeleafProject(
        @PathVariable author: String,
        @PathVariable name: String,
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) platforms: List<String>?,
        @RequestParam(required = false) tab: String?,
    ): ResponseEntity<String> {
        val projectPage = catalogService.project(author, name, tab)
            .copy(backParameters = backContext(query, platforms).searchParameters)
        return html(
            timedRender(renderer = "thymeleaf", page = "project") {
                thymeleafPageRenderer.renderProject(
                    projectPage = projectPage,
                    formAction = THYMELEAF,
                    otherRendererUrl = projectPage.tabUrl(COMPOSE_HTML, projectPage.tab),
                )
            }
        )
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

    private inline fun timedRender(renderer: String, page: String, render: () -> String): String {
        val (html, duration) = measureTimedValue(render)
        logger.info(
            "SSR render renderer={} page={} durationMs={}",
            renderer,
            page,
            duration.inWholeNanoseconds / NANOS_PER_MILLISECOND,
        )
        return html
    }

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
        val logger = LoggerFactory.getLogger(FrontendController::class.java)
        const val NANOS_PER_MILLISECOND = 1_000_000.0

        const val COMPOSE_HTML = "/composehtml"
        const val THYMELEAF = "/thymeleaf"
    }
}

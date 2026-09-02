package com.example.htmlcomparison.hydration

import com.example.htmlcomparison.catalog.CatalogService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import kotlin.time.measureTimedValue

@Controller
class HydratedSearchController(
    private val catalogService: CatalogService,
    private val documentRenderer: SearchDocumentRenderer,
    @param:Value("\${comparison.ssr-url:http://localhost:8080/composehtml}")
    private val ssrComparisonUrl: String,
) {
    @GetMapping("/", produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun page(
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) platforms: List<String>?,
    ): ResponseEntity<String> {
        val state = catalogService.page(query, platforms).toSearchState()
        val (html, duration) = measureTimedValue { documentRenderer.render(state, ssrComparisonUrl) }
        logger.info(
            "SSR render renderer=compose-html-hydrated page=catalog durationMs={}",
            duration.inWholeNanoseconds / NANOS_PER_MILLISECOND,
        )
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_HTML)
            .body(html)
    }

    @GetMapping(SearchApiPath, produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun search(
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) platforms: List<String>?,
    ): ResponseEntity<String> {
        val state = catalogService.page(query, platforms).toSearchState()
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(state.toJson())
    }

    private companion object {
        val logger = LoggerFactory.getLogger(HydratedSearchController::class.java)
        const val NANOS_PER_MILLISECOND = 1_000_000.0
    }
}

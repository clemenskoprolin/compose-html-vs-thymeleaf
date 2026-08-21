package com.example.htmlcomparison.web

import com.example.htmlcomparison.catalog.CatalogService
import com.example.htmlcomparison.web.compose.ComposeHtmlPageRenderer
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.util.UriComponentsBuilder

@Controller
class FrontendController(
    private val catalogService: CatalogService,
    private val composeHtmlPageRenderer: ComposeHtmlPageRenderer,
) {
    @GetMapping("/")
    fun index(): String = "redirect:/composehtml"

    @GetMapping("/composehtml", produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun composeHtml(
        @RequestParam(required = false) query: String?,
    ): ResponseEntity<String> {
        val page = catalogService.page(query)
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_HTML)
            .body(
                composeHtmlPageRenderer.render(
                    page = page,
                    formAction = "/composehtml",
                    otherRendererUrl = comparisonUrl("/thymeleaf", page.query),
                )
            )
    }

    @GetMapping("/thymeleaf")
    fun thymeleaf(
        @RequestParam(required = false) query: String?,
        model: Model,
    ): String {
        val page = catalogService.page(query)
        model.addAttribute("page", page)
        model.addAttribute("formAction", "/thymeleaf")
        model.addAttribute("otherRendererUrl", comparisonUrl("/composehtml", page.query))
        return "catalog"
    }

    private fun comparisonUrl(path: String, query: String): String {
        if (query.isBlank()) return path
        return UriComponentsBuilder.fromPath(path)
            .queryParam("query", query)
            .build()
            .encode()
            .toUriString()
    }
}

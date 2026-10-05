package com.example.htmlcomparison.web.compose

import androidx.compose.runtime.Composable
import com.example.htmlcomparison.catalog.CatalogPage
import com.example.htmlcomparison.catalog.ProjectPage
import com.example.htmlcomparison.web.compose.pages.CatalogPageDocument
import com.example.htmlcomparison.web.compose.pages.ProjectPageDocument
import org.jetbrains.compose.web.composeHtmlToStream
import org.jetbrains.compose.web.composeHtmlToString
import org.springframework.stereotype.Component
import java.io.OutputStream
import java.io.OutputStreamWriter

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

    fun stream(
        page: CatalogPage,
        formAction: String,
        otherRendererUrl: String,
        output: OutputStream,
    ) = streamDocument(output) {
        CatalogPageDocument(page, formAction, otherRendererUrl)
    }

    fun streamProject(
        projectPage: ProjectPage,
        formAction: String,
        otherRendererUrl: String,
        output: OutputStream,
    ) = streamDocument(output) {
        ProjectPageDocument(projectPage, formAction, otherRendererUrl)
    }

    private fun streamDocument(output: OutputStream, content: @Composable () -> Unit) {
        // Spring owns the response stream. Flush each native chunk without closing it.
        val writer = OutputStreamWriter(output, Charsets.UTF_8)
        writer.write("<!doctype html>")
        composeHtmlToStream(sink = { chunk ->
            writer.write(chunk)
            writer.flush()
        }, content = content)
        writer.flush()
    }
}

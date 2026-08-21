package com.example.htmlcomparison.catalog

import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Cleaner
import org.jsoup.safety.Safelist
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

/**
 * Reads a project's README from GitHub and turns it into HTML the page can embed. The markup
 * comes from a third party, so it is run through an allow-list before it reaches a template.
 */
@Service
class GithubReadmeClient(
    private val httpClient: HttpClient,
    @Value("\${klibs.readme.timeout-seconds:8}") timeoutSeconds: Long,
) : ReadmeGateway {
    private val timeout = Duration.ofSeconds(timeoutSeconds)

    private val parser: Parser = Parser.builder()
        .extensions(listOf(TablesExtension.create(), StrikethroughExtension.create()))
        .build()

    private val renderer: HtmlRenderer = HtmlRenderer.builder()
        .extensions(listOf(TablesExtension.create(), StrikethroughExtension.create()))
        .build()

    override fun readme(author: String, name: String): ProjectReadme? {
        val markdown = FILE_NAMES.firstNotNullOfOrNull { fileName -> download(author, name, fileName) }
            ?: return null

        return ProjectReadme(
            html = toSafeHtml(markdown.text, author, name),
            sourceUrl = "https://github.com/${author.urlEncoded()}/${name.urlEncoded()}/blob/HEAD/${markdown.fileName}",
            fileName = markdown.fileName,
        )
    }

    private fun download(author: String, name: String, fileName: String): DownloadedReadme? {
        val uri = URI.create(
            "https://raw.githubusercontent.com/${author.urlEncoded()}/${name.urlEncoded()}/HEAD/$fileName"
        )
        val request = HttpRequest.newBuilder(uri).timeout(timeout).GET().build()
        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() != 200) return null

        return DownloadedReadme(fileName, response.body().take(MAX_CHARACTERS))
    }

    internal fun toSafeHtml(markdown: String, author: String, name: String): String {
        val rendered = renderer.render(parser.parse(markdown))
        val document = Jsoup.parseBodyFragment(rendered)
        document.outputSettings().prettyPrint(false)
        document.absolutiseReferences(author, name)

        val cleaned = Cleaner(SAFELIST).clean(document)
        cleaned.outputSettings().prettyPrint(false)
        return cleaned.body().html()
    }

    /** READMEs link and embed relative to the repository, which the detail page is not. */
    private fun Document.absolutiseReferences(author: String, name: String) {
        val repository = "${author.urlEncoded()}/${name.urlEncoded()}"
        select("img[src]").forEach { image ->
            val source = image.attr("src")
            if (source.isRelative()) {
                image.attr("src", "https://raw.githubusercontent.com/$repository/HEAD/${source.trimStart('/')}")
            }
            image.attr("loading", "lazy")
        }
        select("a[href]").forEach { link ->
            val href = link.attr("href")
            if (href.isRelative() && !href.startsWith("#")) {
                link.attr("href", "https://github.com/$repository/blob/HEAD/${href.trimStart('/')}")
            }
            link.attr("target", "_blank")
            link.attr("rel", "noreferrer")
        }
    }

    /** Anything carrying a scheme is left alone; the allow-list decides whether it survives. */
    private fun String.isRelative(): Boolean =
        isNotBlank() && !startsWith("//") && !SCHEME.containsMatchIn(this)

    private data class DownloadedReadme(
        val fileName: String,
        val text: String,
    )

    private companion object {
        val SCHEME = Regex("^[a-zA-Z][a-zA-Z0-9+.-]*:")
        val FILE_NAMES = listOf("README.md", "readme.md", "README.markdown", "README")
        const val MAX_CHARACTERS = 400_000

        val SAFELIST: Safelist = Safelist.relaxed()
            .addAttributes("a", "target", "rel")
            .addAttributes("img", "loading")
            .addProtocols("img", "src", "https")
    }
}

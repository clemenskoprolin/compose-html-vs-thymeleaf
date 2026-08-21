package com.example.htmlcomparison.web

/**
 * Reduces rendered HTML to a canonical form so the two renderers can be compared for
 * structural equality: whitespace-only text between tags disappears, remaining text is
 * collapsed, and attributes as well as class tokens are sorted.
 */
internal object HtmlNormalizer {
    private val ATTRIBUTE = Regex("""([^\s=/>]+)(?:\s*=\s*"([^"]*)")?""")

    /** `checked`, `checked=""` and `checked="checked"` all mean the same thing in HTML. */
    private val BOOLEAN_ATTRIBUTES = setOf("checked", "disabled", "readonly", "required", "selected", "multiple")

    fun normalize(html: String): String = buildString {
        var index = 0
        while (index < html.length) {
            val open = html.indexOf('<', index)
            if (open < 0) {
                appendText(html.substring(index))
                break
            }
            appendText(html.substring(index, open))

            val close = html.indexOf('>', open)
            if (close < 0) {
                appendText(html.substring(open))
                break
            }
            appendLine(normalizeTag(html.substring(open, close + 1)))
            index = close + 1
        }
    }

    private fun StringBuilder.appendText(raw: String) {
        val text = raw.replace(Regex("\\s+"), " ").trim()
        if (text.isNotEmpty()) appendLine(text.decodedEntities())
    }

    /**
     * The renderers escape text differently — Compose leaves `"` alone where Thymeleaf writes
     * `&quot;` — so text is compared by what it says rather than how it is encoded.
     */
    private fun String.decodedEntities(): String = this
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&#34;", "\"")
        .replace("&#39;", "'")
        .replace("&apos;", "'")
        .replace("&amp;", "&")

    private fun normalizeTag(tag: String): String {
        val body = tag.removePrefix("<").removeSuffix(">").removeSuffix("/").trim()
        if (body.startsWith("!") || body.startsWith("/")) return "<${body.lowercase()}>"

        // Attributes may start on the next line, so the name ends at any whitespace.
        val name = body.takeWhile { !it.isWhitespace() }
        val attributes = ATTRIBUTE.findAll(body.substring(name.length))
            .map { it.groupValues[1] to it.groupValues[2] }
            .map { (attribute, value) ->
                attribute to when {
                    attribute == "class" -> sortedClasses(value)
                    attribute in BOOLEAN_ATTRIBUTES -> ""
                    else -> value
                }
            }
            .sortedBy { it.first }
            .joinToString("") { (attribute, value) -> " $attribute=\"$value\"" }

        return "<${name.lowercase()}$attributes>"
    }

    private fun sortedClasses(value: String): String =
        value.split(' ').filter { it.isNotEmpty() }.sorted().joinToString(" ")
}

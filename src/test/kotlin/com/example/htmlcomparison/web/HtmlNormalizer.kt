package com.example.htmlcomparison.web

/**
 * Reduces rendered HTML to a canonical form so the two renderers can be compared for
 * structural equality: whitespace-only text between tags disappears, remaining text is
 * collapsed, and attributes as well as class tokens are sorted.
 */
internal object HtmlNormalizer {
    private val ATTRIBUTE = Regex("""([^\s=/>]+)(?:\s*=\s*"([^"]*)")?""")

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
        if (text.isNotEmpty()) appendLine(text)
    }

    private fun normalizeTag(tag: String): String {
        val body = tag.removePrefix("<").removeSuffix(">").removeSuffix("/").trim()
        if (body.startsWith("!") || body.startsWith("/")) return "<${body.lowercase()}>"

        val name = body.substringBefore(' ')
        val attributes = ATTRIBUTE.findAll(body.substring(name.length))
            .map { it.groupValues[1] to it.groupValues[2] }
            .map { (attribute, value) -> attribute to if (attribute == "class") sortedClasses(value) else value }
            .sortedBy { it.first }
            .joinToString("") { (attribute, value) -> " $attribute=\"$value\"" }

        return "<${name.lowercase()}$attributes>"
    }

    private fun sortedClasses(value: String): String =
        value.split(' ').filter { it.isNotEmpty() }.sorted().joinToString(" ")
}

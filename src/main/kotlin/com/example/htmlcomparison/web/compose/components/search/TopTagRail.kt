package com.example.htmlcomparison.web.compose.components.search

import androidx.compose.runtime.Composable
import com.example.htmlcomparison.catalog.CatalogPage
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

/** Shortcut GET searches, as klibs.io lists them above its catalog. */
@Composable
internal fun TopTagRail(
    page: CatalogPage,
    formAction: String,
) {
    if (page.topTags.isEmpty()) return

    Div({
        classes("mt-6", "flex", "flex-wrap", "items-center", "gap-2")
        attr("aria-label", "Top tags")
    }) {
        P({ classes("mr-1", "text-xs", "font-bold", "uppercase", "tracking-[0.16em]", "text-subtle") }) {
            Text("Top tags")
        }
        TagChip(page, formAction, tag = "", label = "All")
        page.topTags.forEach { tag -> TagChip(page, formAction, tag = tag, label = tag) }
    }
}

@Composable
private fun TagChip(
    page: CatalogPage,
    formAction: String,
    tag: String,
    label: String,
) {
    val selected = page.query == tag
    A(
        href = page.searchUrl(formAction, tag),
        attrs = {
            classes("inline-flex", "items-center", "rounded-full", "border", "px-3", "py-1.5", "text-xs", "font-semibold", "no-underline", "transition")
            if (selected) {
                classes("border-accent-line", "bg-accent-solid", "text-accent-ink")
            } else {
                classes("border-line-strong", "bg-surface", "text-muted", "hover:border-accent", "hover:text-primary")
            }
            if (selected) attr("aria-current", "page")
        },
    ) {
        Text(label)
    }
}

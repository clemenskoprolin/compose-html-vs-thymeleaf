package com.example.htmlcomparison.catalog

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class CatalogServiceTest {
    @Test
    fun `blank query uses ranked categories without calling MCP`() {
        var calls = 0
        val service = CatalogService(KlibsGateway {
            calls++
            emptyList()
        })

        val page = service.page("  ")

        assertEquals(0, calls)
        assertEquals(0, page.projects.size)
        assertEquals(14, page.categories.size)
        assertEquals("Compose UI", page.categories.first().title)
        assertEquals("compose-multiplatform", page.categories.first().projects.first().name)
        assertEquals("19.3k", page.categories.first().projects.first().stars)
        assertEquals("Local Storage", page.categories[1].title)
        assertNull(page.warning)
    }

    @Test
    fun `search results are shared by the two renderer requests`() {
        var calls = 0
        val result = ProjectCard(
            name = "Result",
            author = "Author",
            description = "Description",
            url = "https://klibs.io/project/Author/Result",
            platforms = listOf("jvm"),
            packages = emptyList(),
            totalPackages = 0,
        )
        val service = CatalogService(KlibsGateway {
            calls++
            listOf(result)
        })

        val composePage = service.page("html")
        val thymeleafPage = service.page("html")

        assertEquals(1, calls)
        assertEquals(composePage, thymeleafPage)
    }

    @Test
    fun `MCP failure becomes a visible fallback instead of a server error`() {
        val service = CatalogService(KlibsGateway { error("network is down") })

        val page = service.page("html")

        assertEquals(3, page.projects.size)
        assertEquals(true, page.warning?.contains("network is down"))
    }
}

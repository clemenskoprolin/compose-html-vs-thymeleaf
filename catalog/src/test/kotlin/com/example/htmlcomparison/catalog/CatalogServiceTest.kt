package com.example.htmlcomparison.catalog

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CatalogServiceTest {
    @Test
    fun `blank query uses ranked categories without calling MCP`() {
        val gateway = FakeKlibsGateway()
        val service = CatalogService(gateway, FakeReadmeGateway())

        val page = service.page("  ")

        assertEquals(emptyList<Pair<String, List<String>>>(), gateway.searches)
        assertEquals(0, page.projects.size)
        assertEquals(14, page.categories.size)
        assertEquals("Compose UI", page.categories.first().title)
        assertEquals("compose-multiplatform", page.categories.first().projects.first().name)
        assertEquals("19.3k", page.categories.first().projects.first().stars)
        assertEquals("Local Storage", page.categories[1].title)
        assertEquals(TopTags, page.topTags)
        assertNull(page.warning)
    }

    @Test
    fun `search results are shared by the two renderer requests`() {
        val gateway = FakeKlibsGateway(onSearch = { _, _ -> listOf(searchResult()) })
        val service = CatalogService(gateway, FakeReadmeGateway())

        val composePage = service.page("html")
        val thymeleafPage = service.page("html")

        assertEquals(1, gateway.searches.size)
        assertEquals(composePage, thymeleafPage)
    }

    @Test
    fun `MCP failure becomes a visible fallback instead of a server error`() {
        val service = CatalogService(FakeKlibsGateway(onSearch = { _, _ -> error("network is down") }), FakeReadmeGateway())

        val page = service.page("html")

        assertEquals(3, page.projects.size)
        assertEquals(true, page.warning?.contains("network is down"))
    }

    @Test
    fun `the platform filter reaches the gateway in canonical form and is cached separately`() {
        val gateway = FakeKlibsGateway(onSearch = { _, _ -> listOf(searchResult()) })
        val service = CatalogService(gateway, FakeReadmeGateway())

        val filtered = service.page("html", listOf("WASM", "unknown", "jvm"))
        service.page("html", listOf("wasm", "jvm"))
        service.page("html")

        // Declaration order, unknown values dropped, and one call per distinct filter.
        assertEquals(listOf(listOf("jvm", "wasm"), emptyList<String>()), gateway.searches.map { it.second })
        assertEquals(listOf("jvm", "wasm"), filtered.platforms)
        assertTrue(filtered.status.contains("on JVM, Wasm"), filtered.status)
    }

    @Test
    fun `the platform filter narrows the featured catalog to projects supporting all of them`() {
        val service = CatalogService(FakeKlibsGateway(), FakeReadmeGateway())

        val unfiltered = service.page("")
        val androidOnly = service.page("", listOf("androidJvm"))

        assertTrue(
            androidOnly.categories.all { category -> category.projects.all { "Android JVM" in it.platforms } },
            androidOnly.categories.toString(),
        )
        assertTrue(
            androidOnly.projectCount() < unfiltered.projectCount(),
            "${androidOnly.projectCount()} of ${unfiltered.projectCount()}",
        )
        assertNull(androidOnly.warning)
    }

    @Test
    fun `a project lookup is cached and reused by the two renderer requests`() {
        val project = searchResult()
        val gateway = FakeKlibsGateway(onFind = { _, _ -> project })
        val service = CatalogService(gateway, FakeReadmeGateway())

        val first = service.project("Author", "Result")
        val second = service.project("Author", "Result")

        assertEquals(listOf("Author" to "Result"), gateway.lookups)
        assertSame(project, first.project)
        assertEquals(first, second)
        assertNull(first.warning)
    }

    @Test
    fun `an unknown project explains itself instead of failing`() {
        val service = CatalogService(FakeKlibsGateway(), FakeReadmeGateway())

        val page = service.project("Nobody", "nothing")

        assertNull(page.project)
        assertEquals("klibs.io has no project called “Nobody/nothing”.", page.warning)
    }

    @Test
    fun `a failed project lookup surfaces the reason`() {
        val service = CatalogService(FakeKlibsGateway(onFind = { _, _ -> error("network is down") }), FakeReadmeGateway())

        val page = service.project("JetBrains", "compose-multiplatform")

        assertNull(page.project)
        assertEquals(true, page.warning?.contains("network is down"))
    }

    private fun CatalogPage.projectCount(): Int = categories.sumOf { it.projects.size }

    private fun searchResult() = ProjectCard(
        name = "Result",
        author = "Author",
        description = "Description",
        url = "https://klibs.io/project/Author/Result",
        platforms = listOf("jvm"),
        packages = emptyList(),
        totalPackages = 0,
    )
}

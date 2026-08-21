package com.example.htmlcomparison.catalog

internal class FakeKlibsGateway(
    private val onSearch: (String, List<String>) -> List<ProjectCard> = { _, _ -> emptyList() },
    private val onFind: (String, String) -> ProjectCard? = { _, _ -> null },
) : KlibsGateway {
    val searches = mutableListOf<Pair<String, List<String>>>()
    val lookups = mutableListOf<Pair<String, String>>()

    override fun searchProjects(query: String, platforms: List<String>): List<ProjectCard> {
        searches += query to platforms
        return onSearch(query, platforms)
    }

    override fun findProject(author: String, name: String): ProjectCard? {
        lookups += author to name
        return onFind(author, name)
    }
}

internal class FakeReadmeGateway(
    private val onReadme: (String, String) -> ProjectReadme? = { _, _ -> null },
) : ReadmeGateway {
    val lookups = mutableListOf<Pair<String, String>>()

    override fun readme(author: String, name: String): ProjectReadme? {
        lookups += author to name
        return onReadme(author, name)
    }
}

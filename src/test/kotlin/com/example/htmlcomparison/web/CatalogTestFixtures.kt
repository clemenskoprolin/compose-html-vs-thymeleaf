package com.example.htmlcomparison.web

import com.example.htmlcomparison.catalog.CatalogPage
import com.example.htmlcomparison.catalog.ProjectCard
import com.example.htmlcomparison.catalog.ProjectCategory
import com.example.htmlcomparison.catalog.ProjectPackage
import com.example.htmlcomparison.catalog.RankedProject
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

internal fun projectWithOverflowingMetadata() = ProjectCard(
    name = "Example",
    author = "Example Author",
    description = "An example project",
    url = "https://klibs.io/example",
    platforms = (1..7).map { "platform-$it" },
    packages = (1..4).map { index ->
        ProjectPackage(
            groupId = "example",
            artifactId = "package-$index",
            latestVersion = "1.0.$index",
            latestStableVersion = null,
        )
    },
    totalPackages = 6,
)

internal fun featuredTestCategories(): List<ProjectCategory> = listOf(
    ProjectCategory(
        title = "Compose UI",
        slug = "compose-ui",
        projects = listOf(
            RankedProject(
                name = "compose-multiplatform",
                author = "JetBrains",
                stars = "19.3k",
                description = "Share declarative interfaces across platforms.",
                tags = listOf("#compose-ui", "#compose", "#ui", "#android", "#apple"),
                platforms = listOf("Android JVM", "Kotlin/Native", "Wasm"),
                license = "Apache License 2.0",
            ),
        ),
    ),
    ProjectCategory(
        title = "Local Storage",
        slug = "local-storage",
        projects = listOf(
            RankedProject("Store", "MobileNativeFoundation", "3.4k", grantWinner = true),
        ),
    ),
)

internal object ThymeleafTestRenderer {
    private val templateEngine = SpringTemplateEngine().apply {
        setTemplateResolver(
            ClassLoaderTemplateResolver().apply {
                prefix = "templates/"
                suffix = ".html"
                templateMode = TemplateMode.HTML
                characterEncoding = Charsets.UTF_8.name()
                isCacheable = false
            }
        )
    }

    fun render(
        page: CatalogPage,
        formAction: String,
        otherRendererUrl: String,
    ): String {
        val context = Context().apply {
            setVariable("page", page)
            setVariable("formAction", formAction)
            setVariable("otherRendererUrl", otherRendererUrl)
        }
        return templateEngine.process("catalog", context)
    }
}

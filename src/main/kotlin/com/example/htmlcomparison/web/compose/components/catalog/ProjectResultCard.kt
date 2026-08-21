package com.example.htmlcomparison.web.compose.components.catalog

import androidx.compose.runtime.Composable
import com.example.htmlcomparison.catalog.ProjectCard
import com.example.htmlcomparison.catalog.ProjectPackage
import org.jetbrains.compose.web.attributes.ARel
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.rel
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Article
import org.jetbrains.compose.web.dom.Code
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
internal fun ProjectResultCard(project: ProjectCard) {
    Article({ classes("group", "flex", "min-w-0", "h-full", "flex-col", "rounded-3xl", "border", "border-line", "bg-card", "p-6", "transition", "hover:-translate-y-0.5", "hover:border-accent-line", "hover:bg-card-hover") }) {
        ProjectCardHeading(project)
        P({ classes("mt-4", "grow", "text-sm", "leading-6", "text-muted") }) { Text(project.description) }
        PlatformBadges(project.displayedPlatforms)
        PackageList(
            packages = project.displayedPackages,
            additionalPackageCount = project.additionalPackageCount,
        )
    }
}

@Composable
private fun ProjectCardHeading(project: ProjectCard) {
    Div({ classes("flex", "items-start", "justify-between", "gap-4") }) {
        Div({ classes("min-w-0") }) {
            P({ classes("text-xs", "font-bold", "uppercase", "tracking-[0.16em]", "text-subtle") }) { Text(project.author) }
            H3({ classes("mt-2", "break-words", "text-2xl", "font-black", "text-primary") }) { Text(project.name) }
        }
        A(
            href = project.url,
            attrs = {
                classes("rounded-full", "border", "border-line", "px-3", "py-1.5", "text-xs", "font-bold", "text-accent", "no-underline", "group-hover:border-accent-line")
                target(ATarget.Blank)
                rel(ARel.NoReferrer)
            },
        ) {
            Text("klibs.io \u2197")
        }
    }
}

@Composable
private fun PlatformBadges(platforms: List<String>) {
    Div({ classes("mt-5", "flex", "flex-wrap", "gap-2") }) {
        platforms.forEach { platform ->
            Span({ classes("rounded-full", "bg-secondary-soft", "px-2.5", "py-1", "text-[11px]", "font-bold", "uppercase", "tracking-wide", "text-secondary") }) {
                Text(platform)
            }
        }
    }
}

@Composable
private fun PackageList(
    packages: List<ProjectPackage>,
    additionalPackageCount: Int,
) {
    Div({ classes("mt-6", "space-y-2", "border-t", "border-line", "pt-5") }) {
        packages.forEach { packageInfo -> PackageRow(packageInfo) }
        if (additionalPackageCount > 0) {
            P({ classes("pt-1", "text-xs", "text-subtle") }) { Text("+$additionalPackageCount more packages") }
        }
    }
}

@Composable
private fun PackageRow(packageInfo: ProjectPackage) {
    Div({ classes("flex", "min-w-0", "items-center", "justify-between", "gap-4", "rounded-xl", "bg-code", "px-3", "py-2.5") }) {
        Code({ classes("min-w-0", "truncate", "text-xs", "text-muted") }) { Text(packageInfo.coordinate) }
        Span({ classes("shrink-0", "text-xs", "font-bold", "text-accent") }) { Text(packageInfo.displayVersion) }
    }
}

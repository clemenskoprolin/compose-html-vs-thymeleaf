package com.example.htmlcomparison.web.compose.components.project

import androidx.compose.runtime.Composable
import com.example.htmlcomparison.catalog.ProjectCard
import com.example.htmlcomparison.catalog.ProjectPackage
import org.jetbrains.compose.web.dom.Code
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Ul

@Composable
internal fun ProjectPackages(project: ProjectCard) {
    Section({ classes("rounded-3xl", "border", "border-line", "bg-card", "p-6") }) {
        Div({ classes("flex", "flex-wrap", "items-end", "justify-between", "gap-3") }) {
            H2({ classes("text-2xl", "font-black", "tracking-tight", "text-primary") }) { Text("Packages") }
            P({ classes("text-sm", "text-subtle") }) { Text(project.packageSummary) }
        }

        if (project.packages.isEmpty()) {
            P({ classes("mt-5", "text-sm", "text-muted") }) { Text("This project publishes no packages the MCP server knows about.") }
            return@Section
        }

        Ul({ classes("mt-5", "space-y-3") }) {
            project.packages.forEach { packageInfo -> PackageRow(packageInfo) }
        }
    }
}

@Composable
private fun PackageRow(packageInfo: ProjectPackage) {
    Li({ classes("rounded-2xl", "border", "border-line", "bg-surface", "p-4") }) {
        Div({ classes("flex", "flex-wrap", "items-start", "justify-between", "gap-3") }) {
            H3({ classes("min-w-0", "break-all", "text-sm", "font-bold", "text-primary") }) { Text(packageInfo.coordinate) }
            Span({ classes("shrink-0", "rounded-full", "bg-secondary-soft", "px-2.5", "py-1", "text-[11px]", "font-bold", "text-secondary") }) {
                Text(packageInfo.displayVersion)
            }
        }
        if (packageInfo.description.isNotBlank()) {
            P({ classes("mt-2", "text-sm", "leading-6", "text-muted") }) { Text(packageInfo.description) }
        }
        Div({ classes("mt-3", "overflow-x-auto", "rounded-xl", "bg-code", "px-3", "py-2.5") }) {
            Code({ classes("whitespace-pre", "text-xs", "text-muted") }) { Text(packageInfo.dependencyNotation) }
        }
        if (packageInfo.hasPrerelease) {
            P({ classes("mt-2", "text-xs", "text-subtle") }) {
                Text("Prerelease available: ${packageInfo.latestVersion}")
            }
        }
    }
}

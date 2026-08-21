package com.example.htmlcomparison.catalog

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import tools.jackson.databind.JsonNode
import tools.jackson.databind.json.JsonMapper
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.concurrent.atomic.AtomicLong

@Service
class KlibsMcpClient(
    private val jsonMapper: JsonMapper,
    private val httpClient: HttpClient,
    @Value("\${klibs.mcp.url:https://api.klibs.io/mcp}") endpointUrl: String,
    @Value("\${klibs.mcp.timeout-seconds:12}") timeoutSeconds: Long,
) : KlibsGateway {
    private val endpoint = URI.create(endpointUrl)
    private val timeout = Duration.ofSeconds(timeoutSeconds)
    private val requestIds = AtomicLong(1)
    private val sessionLock = Any()

    @Volatile
    private var currentSession: McpSession? = null

    override fun searchProjects(query: String): List<ProjectCard> {
        val session = session()
        val request = mapOf(
            "jsonrpc" to "2.0",
            "id" to requestIds.incrementAndGet(),
            "method" to "tools/call",
            "params" to mapOf(
                "name" to "searchProjects",
                "arguments" to mapOf(
                    "query" to query,
                    "maxPackagesPerProject" to 3,
                ),
            ),
        )

        val response = postJson(request, session)
        return parseToolResponse(response.body)
    }

    internal fun parseToolResponse(responseBody: String): List<ProjectCard> {
        val envelope = jsonMapper.readTree(jsonPayload(responseBody))
        val rpcError = envelope.path("error")
        if (!rpcError.isMissingNode && !rpcError.isNull) {
            throw IllegalStateException("MCP error: ${rpcError.path("message").asString("unknown error")}")
        }

        val result = envelope.path("result")
        val contentText = result.path("content")
            .firstOrNull { it.path("type").asString("") == "text" }
            ?.path("text")
            ?.asString()
            ?: throw IllegalStateException("MCP tool response did not contain text content")

        if (result.path("isError").asBoolean(false)) {
            throw IllegalStateException("klibs.io tool error: $contentText")
        }

        val payload = jsonMapper.readTree(contentText)
        return payload.path("projects").children().map { project ->
            val author = project.requiredText("projectAuthor")
            val name = project.requiredText("projectName")
            ProjectCard(
                name = name,
                author = author,
                description = project.nullableText("description") ?: "No project description is available yet.",
                url = "https://klibs.io/project/${author.urlSegment()}/${name.urlSegment()}",
                platforms = project.path("platforms").children().map(JsonNode::asString),
                packages = project.path("packages").children().map { packageNode ->
                    ProjectPackage(
                        groupId = packageNode.requiredText("groupId"),
                        artifactId = packageNode.requiredText("artifactId"),
                        latestVersion = packageNode.requiredText("latestVersion"),
                        latestStableVersion = packageNode.nullableText("latestStableVersion"),
                    )
                },
                totalPackages = project.path("totalPackages").asInt(),
            )
        }
    }

    private fun session(): McpSession = currentSession ?: synchronized(sessionLock) {
        currentSession ?: initialize().also { currentSession = it }
    }

    private fun initialize(): McpSession {
        val initializeRequest = mapOf(
            "jsonrpc" to "2.0",
            "id" to requestIds.incrementAndGet(),
            "method" to "initialize",
            "params" to mapOf(
                "protocolVersion" to PROTOCOL_VERSION,
                "capabilities" to emptyMap<String, Any>(),
                "clientInfo" to mapOf(
                    "name" to "compose-html-vs-thymeleaf",
                    "version" to "0.1.0",
                ),
            ),
        )

        val response = postJson(initializeRequest, session = null)
        val envelope = jsonMapper.readTree(jsonPayload(response.body))
        val negotiatedProtocol = envelope.path("result").path("protocolVersion").asString(PROTOCOL_VERSION)
        val initializedSession = McpSession(
            protocolVersion = negotiatedProtocol,
            sessionId = response.sessionId,
        )

        postJson(
            body = mapOf(
                "jsonrpc" to "2.0",
                "method" to "notifications/initialized",
            ),
            session = initializedSession,
            responseBodyRequired = false,
        )

        return initializedSession
    }

    private fun postJson(
        body: Any,
        session: McpSession?,
        responseBodyRequired: Boolean = true,
    ): McpHttpResponse {
        val requestBuilder = HttpRequest.newBuilder(endpoint)
            .timeout(timeout)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json, text/event-stream")
            .POST(HttpRequest.BodyPublishers.ofString(jsonMapper.writeValueAsString(body)))

        session?.protocolVersion?.let { requestBuilder.header("MCP-Protocol-Version", it) }
        session?.sessionId?.let { requestBuilder.header("Mcp-Session-Id", it) }

        val response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() !in 200..299) {
            throw IllegalStateException(
                "klibs.io MCP returned HTTP ${response.statusCode()}: ${response.body().take(240)}"
            )
        }
        if (responseBodyRequired && response.body().isBlank()) {
            throw IllegalStateException("klibs.io MCP returned an empty response")
        }

        return McpHttpResponse(
            body = response.body(),
            sessionId = response.headers().firstValue("Mcp-Session-Id").orElse(null),
        )
    }

    private fun jsonPayload(responseBody: String): String {
        val trimmed = responseBody.trim()
        if (trimmed.startsWith("{") || trimmed.startsWith("[")) return trimmed

        return trimmed.lineSequence()
            .map(String::trim)
            .firstOrNull { it.startsWith("data:") }
            ?.removePrefix("data:")
            ?.trim()
            ?: throw IllegalStateException("Unsupported MCP response format")
    }

    private fun JsonNode.requiredText(field: String): String = path(field)
        .asString("")
        .takeIf(String::isNotBlank)
        ?: throw IllegalStateException("klibs.io response is missing '$field'")

    private fun JsonNode.nullableText(field: String): String? = path(field)
        .takeUnless { it.isMissingNode || it.isNull }
        ?.asString()
        ?.takeIf(String::isNotBlank)

    private fun JsonNode.children(): List<JsonNode> = iterator().asSequence().toList()

    private fun String.urlSegment(): String = URLEncoder.encode(this, StandardCharsets.UTF_8)
        .replace("+", "%20")

    private data class McpSession(
        val protocolVersion: String,
        val sessionId: String?,
    )

    private data class McpHttpResponse(
        val body: String,
        val sessionId: String?,
    )

    companion object {
        private const val PROTOCOL_VERSION = "2025-06-18"
    }
}

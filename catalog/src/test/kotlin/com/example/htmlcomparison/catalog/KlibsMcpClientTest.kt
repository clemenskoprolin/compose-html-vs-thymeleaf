package com.example.htmlcomparison.catalog

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import tools.jackson.databind.json.JsonMapper
import java.net.http.HttpClient

class KlibsMcpClientTest {
    private val client = KlibsMcpClient(
        jsonMapper = JsonMapper.builder().build(),
        httpClient = HttpClient.newHttpClient(),
        endpointUrl = "https://example.invalid/mcp",
        timeoutSeconds = 1,
    )

    @Test
    fun `parses nested text content returned by MCP tool call`() {
        val response = """
            {
              "jsonrpc": "2.0",
              "id": 2,
              "result": {
                "content": [{
                  "type": "text",
                  "text": "{\"projects\":[{\"projectName\":\"Ksoup\",\"projectAuthor\":\"MohamedRejeb\",\"description\":\"HTML parser\",\"platforms\":[\"common\",\"jvm\"],\"targets\":[],\"packages\":[{\"groupId\":\"com.mohamedrejeb.ksoup\",\"artifactId\":\"ksoup-html\",\"latestVersion\":\"0.6.0\",\"latestStableVersion\":null,\"description\":null}],\"totalPackages\":1}]}"
                }],
                "isError": false
              }
            }
        """.trimIndent()

        val project = client.parseToolResponse(response).single()

        assertEquals("Ksoup", project.name)
        assertEquals(listOf("common", "jvm"), project.platforms)
        assertEquals("com.mohamedrejeb.ksoup:ksoup-html", project.packages.single().coordinate)
        assertEquals("0.6.0", project.packages.single().displayVersion)
    }
}


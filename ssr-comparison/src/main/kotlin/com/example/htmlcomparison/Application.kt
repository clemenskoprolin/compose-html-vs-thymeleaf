package com.example.htmlcomparison

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.net.http.HttpClient
import java.time.Duration

@SpringBootApplication
class Application {
    @Bean
    fun httpClient(): HttpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build()
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}


package com.autenticacion.GenoSentinelAuth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class ApiClient {

    private final WebClient webClient;

    public String get(String url) {
        return call(() ->
                webClient.get()
                        .uri(url)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(String.class)
        );
    }

    public String post(String url, Object requestBody) {
        return call(() ->
                webClient.post()
                        .uri(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(String.class)
        );
    }
    public String patch(String url, Object requestBody) {
        return call(() ->
                webClient.post()
                        .uri(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(String.class)
        );
    }

    public String patch(String url) {
        return call(() ->
                webClient.patch()
                        .uri(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(String.class)
        );
    }

    public String put(String url, Object requestBody) {
        return call(() ->
                webClient.put()
                        .uri(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(String.class)
        );
    }

    public String delete(String url) {
        return call(() ->
                webClient.delete()
                        .uri(url)
                        .retrieve()
                        .bodyToMono(String.class)
        );
    }

    private String call(Supplier<Mono<String>> request) {
        try {
            return request.get()
                    .onErrorResume(e -> Mono.error(
                            new RuntimeException("External microservice error: " + e.getMessage())
                    ))
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Error contacting microservice: " + e.getMessage(), e);
        }
    }
}
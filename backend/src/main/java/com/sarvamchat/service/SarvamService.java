package com.sarvamchat.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SarvamService {

    private final WebClient sarvamWebClient;

    @Value("${sarvam.api.model}")
    private String model;

    @Value("${sarvam.api.temperature}")
    private double temperature;

    @Value("${sarvam.api.max-tokens}")
    private int maxTokens;

    public SarvamService(WebClient sarvamWebClient) {
        this.sarvamWebClient = sarvamWebClient;
    }

    public String complete(List<Map<String, String>> messages) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", messages);
        body.put("temperature", temperature);
        body.put("max_tokens", maxTokens);

        try {
            Map<?, ?> response = sarvamWebClient.post()
                    .uri("/v1/chat/completions")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return extractReply(response);
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Sarvam API error " + e.getStatusCode() + ": " + e.getResponseBodyAsString(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private String extractReply(Map<?, ?> response) {
        if (response == null) {
            throw new RuntimeException("Empty response from Sarvam API");
        }
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        if (choices == null || choices.isEmpty()) {
            throw new RuntimeException("No choices in Sarvam response: " + response);
        }
        Map<String, Object> first = choices.get(0);
        Map<String, Object> message = (Map<String, Object>) first.get("message");
        if (message == null) {
            throw new RuntimeException("No message in Sarvam choice: " + first);
        }
        Object content = message.get("content");
        return content == null ? "" : content.toString();
    }
}

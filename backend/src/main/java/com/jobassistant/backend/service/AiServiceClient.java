package com.jobassistant.backend.service;

import com.jobassistant.backend.dto.ChatRequest;
import com.jobassistant.backend.dto.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * Talks to the Python AI microservice, which owns the actual LLM call
 * (and, in later phases, the Job/Resume/Application agent logic).
 */
@Service
public class AiServiceClient {

    private final WebClient webClient;

    public AiServiceClient(@Value("${ai.service.base-url}") String aiServiceBaseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(aiServiceBaseUrl)
                .build();
    }

    public ChatResponse getReply(ChatRequest request) {
        try {
            return webClient.post()
                    .uri("/chat")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ChatResponse.class)
                    .block();
        } catch (WebClientResponseException ex) {
            throw new AiServiceException(
                    "AI service returned an error: " + ex.getStatusCode(), ex);
        } catch (Exception ex) {
            throw new AiServiceException("Could not reach AI service", ex);
        }
    }

    public static class AiServiceException extends RuntimeException {
        public AiServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

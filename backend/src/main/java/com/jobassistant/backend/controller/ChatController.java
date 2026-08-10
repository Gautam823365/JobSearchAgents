package com.jobassistant.backend.controller;

import com.jobassistant.backend.dto.ChatRequest;
import com.jobassistant.backend.dto.ChatResponse;
import com.jobassistant.backend.service.AiServiceClient;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final AiServiceClient aiServiceClient;

    public ChatController(AiServiceClient aiServiceClient) {
        this.aiServiceClient = aiServiceClient;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok", "service", "backend");
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        ChatResponse response = aiServiceClient.getReply(request);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(AiServiceClient.AiServiceException.class)
    public ResponseEntity<Map<String, String>> handleAiServiceException(
            AiServiceClient.AiServiceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("error", ex.getMessage()));
    }
}

package com.dao.cookbook.service;

import com.dao.cookbook.dto.response.AIChatResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AIService {

    @Value("${ai.service.url:http://localhost:8001}")
    private String aiServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public AIChatResponseDTO chat(String question) {
        try {
            // Tạo request body
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("question", question);

            // Tạo headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Tạo HTTP entity
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            // Gọi Python AI service
            String url = aiServiceUrl + "/chat";
            AIChatResponseDTO response = restTemplate.postForObject(url, entity, AIChatResponseDTO.class);

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi gọi AI service: " + e.getMessage(), e);
        }
    }
}

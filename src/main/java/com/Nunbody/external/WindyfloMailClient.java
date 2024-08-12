package com.Nunbody.external;

import com.Nunbody.global.config.RestTemplateConfig;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class WindyfloMailClient {
    private final String FIND_MAIL_URL = "https://windyflo.com/api/v1/prediction/05d769b1-0a34-49c5-83d3-6b92c1d650f8";
    private final String CREATE_MAIL_URL = "https://flowise-1-6z96.onrender.com/api/v1/prediction/187dedc3-a56f-4619-a4e8-6fe17dc82d4d";
    private final String SUMMARY_MAIL_URL = "https://flowise-1-6z96.onrender.com/api/v1/prediction/01554e84-e710-4424-8aef-085531ddd0f4";

    private final RestTemplate restTemplate;

    public ConversationQARes findMailInVectorDB(String question) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        QuestionRequest requestBody = new QuestionRequest(question);
        HttpEntity<QuestionRequest> request = new HttpEntity<>(requestBody, headers);

        return restTemplate.postForObject(FIND_MAIL_URL, request, ConversationQARes.class);
    }
    public JsonNode createMail(String question) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        QuestionRequest requestBody = new QuestionRequest(question);
        HttpEntity<QuestionRequest> request = new HttpEntity<>(requestBody, headers);

        JsonNode ss =  restTemplate.postForObject(CREATE_MAIL_URL, request, JsonNode.class);
        return ss;
    }
    public JsonNode summaryMail(String question) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        QuestionRequest requestBody = new QuestionRequest(question);
        HttpEntity<QuestionRequest> request = new HttpEntity<>(requestBody, headers);

        JsonNode ss =  restTemplate.postForObject(SUMMARY_MAIL_URL, request, JsonNode.class);
        return ss;
    }
}

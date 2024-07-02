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
    private final String FIND_MAIL_URL = "https://windyflo.com/api/v1/prediction/a7800245-8f8e-4933-bab2-7776c22a5da8";
    private final String CREATE_MAIL_URL = "https://windyflo.com/api/v1/prediction/2b8a91d7-cdfe-434b-9672-d9bb3cb6faa1";
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
}

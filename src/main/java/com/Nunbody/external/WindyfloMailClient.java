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
    private final String FIND_MAIL_URL = "https://windyflo.com/api/v1/prediction/2eb15033-681a-410b-8577-2b69d8e309d4";
    private final String CREATE_MAIL_URL = "https://windyflo.com/api/v1/prediction/f66b8ade-2393-4a1a-ae48-a23c258b460a";
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

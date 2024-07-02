package com.Nunbody.domain.Windyflo.service;

import com.Nunbody.domain.Windyflo.dto.req.WindyfloReq;
import com.Nunbody.domain.Windyflo.dto.res.EmailResDto;
import com.Nunbody.external.ConversationQARes;
import com.Nunbody.external.WindyfloMailClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class WindyfloService {
    private final WindyfloMailClient windyfloMailClient;
    private final ObjectMapper objectMapper;

    public EmailResDto createMail(WindyfloReq windyfloReq) throws IOException {
        EmailResDto emailResDto = new EmailResDto("", "", "");
        while (emailResDto.template().isEmpty() || emailResDto.header().isEmpty() || emailResDto.body().isEmpty()) {
            ConversationQARes result = windyfloMailClient.findMailInVectorDB(windyfloReq.prompt());
//            emailResDto = extractEmailTemplate(result.getText());
            if (result.getText().equals("Hmm, I'm not sure")) {
                emailResDto = extractEmailTemplate(windyfloMailClient.createMail(windyfloReq.prompt()));
            }
            else
                emailResDto = extractEmailTemplate(result.getText());
//            System.out.println(result.getText());
        }
        return emailResDto;
    }

    public EmailResDto extractEmailTemplate(Object input) throws IOException {
        String inputString = input instanceof String ? (String) input : input.toString();

        try {
            JsonNode rootNode = objectMapper.readTree(inputString);

            // 'json' 키가 있는지 확인
            if (rootNode.has("json")) {
                JsonNode jsonNode = rootNode.get("json");

                if (jsonNode.isObject() && jsonNode.has("template") && jsonNode.has("header") && jsonNode.has("body")) {
                    String format = jsonNode.path("template").asText();
                    String subject = jsonNode.path("header").asText();
                    String body = jsonNode.path("body").asText();

                    return EmailResDto.of(format, subject, body);
                }
            }

            // 'json' 키가 없거나 필요한 필드가 없는 경우, 루트 노드에서 직접 확인
            if (rootNode.isObject() && rootNode.has("template") && rootNode.has("header") && rootNode.has("body")) {
                String format = rootNode.path("template").asText();
                String subject = rootNode.path("header").asText();
                String body = rootNode.path("body").asText();

                return EmailResDto.of(format, subject, body);
            }
        } catch (JsonProcessingException e) {
            // JSON 파싱 실패 시 텍스트 파싱 로직으로 넘어감
        }

        // JSON 파싱 실패 또는 필요한 필드가 없는 경우 텍스트 파싱 시도
        String format = extractValue(inputString, "형식: (.+)");
        String subject = extractValue(inputString, "제목: (.+)");
        String body = extractValue(inputString, "본문: \n```\n([\\s\\S]*?)\n```");

        if (format.isEmpty() && subject.isEmpty() && body.isEmpty()) {
            // 텍스트 파싱도 실패한 경우, 전체 입력을 본문으로 사용
            return EmailResDto.of("", "", inputString);
        }

        return EmailResDto.of(format, subject, body);
    }

    private String extractValue(String data, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);
        return matcher.find() ? matcher.group(1) : "";
    }
}
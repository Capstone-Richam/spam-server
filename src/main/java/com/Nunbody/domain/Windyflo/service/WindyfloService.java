package com.Nunbody.domain.Windyflo.service;

import com.Nunbody.domain.Windyflo.dto.req.WindyfloReq;
import com.Nunbody.domain.Windyflo.dto.res.EmailResDto;
import com.Nunbody.external.ConversationQARes;
import com.Nunbody.external.WindyfloMailClient;
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
        EmailResDto emailResDto = new EmailResDto("","","");
        while(emailResDto.template().isEmpty() || emailResDto.header().isEmpty() || emailResDto.body().isEmpty() ) {
            ConversationQARes result = windyfloMailClient.findMailInVectorDB(windyfloReq.prompt());
            if (result.getText().equals("Hmm, I'm not sure")) {
                result = windyfloMailClient.createMail(windyfloReq.prompt());
            }
            emailResDto = extractEmailTemplate(result.getText());
        }
        return emailResDto;
    }
    public EmailResDto extractEmailTemplate(String input) throws IOException {
        // JSON 형식인지 확인
        if (input.trim().startsWith("{")) {
            // JSON 파싱 로직
            JsonNode rootNode = objectMapper.readTree(input);
            String data = rootNode.path("data").asText();

        String format = extractValue(data, "형식: (.+)");
        String subject = extractValue(data, "제목: (.+)");
        String body = extractValue(data, "본문: \n```\n([\\s\\S]*?)\n```");

            return EmailResDto.of(format, subject, body);
        } else {
            // 텍스트 파싱 로직
            String format = extractValue(input, "형식: (.+)");
            String subject = extractValue(input, "제목: (.+)");
            String body = extractValue(input, "본문: \n```\n([\\s\\S]*?)\n```");
            return EmailResDto.of(format, subject, body);
        }
    }

    private String extractValue(String data, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);
        return matcher.find() ? matcher.group(1) : "";
    }
}

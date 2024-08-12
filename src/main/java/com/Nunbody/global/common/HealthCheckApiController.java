package com.Nunbody.global.common;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class HealthCheckApiController {
    @RequestMapping("/")
    public String MeetUpServer() {
        return "스팸보다 리챔";
    }
    @Value("${app.kakao.client.id}")
    private String KAKAO_CLIENT_ID;

    @Value("${app.kakao.client.secret}")
    private String KAKAO_CLIENT_SECRET;

    @Value("${app.kakao.callback.url}")
    private String KAKAO_REDIRECT_URI;
    private static final String KAKAO_AUTH_ENDPOINT = "https://kauth.kakao.com/oauth/authorize";

    @GetMapping("/oauth/kakao")
    public String kakaoOauth(HttpServletResponse response) throws IOException {
        String scope = "talk_message,friends"; // 카카오톡 메시지 전송과 친구 목록 접근을 위한 scope

        String authUrl = KAKAO_AUTH_ENDPOINT +
                "?client_id=" + KAKAO_CLIENT_ID +
                "&redirect_uri=" + KAKAO_REDIRECT_URI +
                "&response_type=code" +
                "&scope=" + scope;

        response.sendRedirect(authUrl);
        return response.encodeRedirectURL(authUrl);
    }
    @GetMapping("/kakao-login")
    public ResponseEntity<Map<String, String>> initiateKakaoLogin() {
        String state = generateRandomState(); // 보안을 위한 랜덤 상태 생성
        String scope = "talk_message,friends";
        String authUrl = KAKAO_AUTH_ENDPOINT +
                "?client_id=" + KAKAO_CLIENT_ID +
                "&redirect_uri=" + KAKAO_REDIRECT_URI +
                "&response_type=code" +
                "&scope=" + scope;

        Map<String, String> response = new HashMap<>();
        response.put("loginUrl", authUrl);
        response.put("state", state);

        return ResponseEntity.ok(response);
    }
    private String generateRandomState() {
        return UUID.randomUUID().toString();
    }
}

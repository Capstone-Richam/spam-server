package com.Nunbody.domain.member.service;


import com.Nunbody.domain.member.domain.Member;
import com.Nunbody.jwt.JwtTokenProvider;
import com.Nunbody.token.OAuthToken;
import com.Nunbody.token.TokenInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final JwtTokenProvider jwtProvider;


    public OAuthToken getAccessToken(ResponseEntity<String> response) throws JsonProcessingException {

        return objectMapper.readValue(response.getBody(), OAuthToken.class);
    }

    public ResponseEntity<String> createGetRequest(String socialLoginType, OAuthToken oAuthToken) {
        String url;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + oAuthToken.getAccessToken());

        if ("kakao".equals(socialLoginType)) {
            // 카카오 URL을 선택
            url = "https://kapi.kakao.com/v2/user/me";
        } else if ("google".equals(socialLoginType)) {
            // Google URL을 선택
            url = "https://www.googleapis.com/oauth2/v1/userinfo";
        } else {
            // socialLoginType이 지원되지 않는 경우 예외처리 또는 기본 URL 설정
            throw new IllegalArgumentException("Unsupported social login type: " + socialLoginType);
        }

        HttpEntity<String> request = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.GET, request, String.class);
    }


    public ResponseEntity<String> createGetRequest(OAuthToken oAuthToken) {
        String url = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + oAuthToken.getAccessToken());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        return response;
    }
//    public GoogleUser getGoogleUserInfo(ResponseEntity<String> userInfoResponse) throws JsonProcessingException{
//
//        return objectMapper.readValue(userInfoResponse.getBody(), GoogleUser.class);
//    }
//    public KakaoUser getKakaoUserInfo(ResponseEntity<String> userInfoResponse) throws JsonProcessingException{
//
//        return objectMapper.readValue(userInfoResponse.getBody(), KakaoUser.class);
//    }

    public TokenInfo issueAccessTokenAndRefreshToken(Member member) {
        return jwtProvider.issueToken(member.getId());
    }
}
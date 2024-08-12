package com.Nunbody.domain.Kakao.service;

import com.Nunbody.domain.Kakao.service.dto.DefaultMessageDto;
import com.Nunbody.domain.Kakao.service.dto.Friend;
import com.Nunbody.external.KakaoFriendsResponse;
import com.Nunbody.external.KakaoMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;



@Service
@RequiredArgsConstructor
public class KakaoService {
//    private Logger logger = LoggerFactory.getLogger(this.getClass());
private static final String APP_TYPE_URL_ENCODED = "application/x-www-form-urlencoded;charset=UTF-8";
    private static final String APP_TYPE_JSON = "application/json;charset=UTF-8";
    private static final String KAKAO_TOKEN_ENDPOINT = "https://kauth.kakao.com/oauth/token";

    public static String authToken;
    @Value("${app.kakao.client.id}")
    private String KAKAO_CLIENT_ID;

    @Value("${app.kakao.client.secret}")
    private String KAKAO_CLIENT_SECRET;

    @Value("${app.kakao.callback.url}")
    private String KAKAO_REDIRECT_URI;
    private final RestTemplate restTemplate;
    private final KakaoMessage kakaoMessage;

    public String getAuthToken(String code) throws JsonProcessingException {
        String jsonResponse = restTemplate.postForObject(KAKAO_TOKEN_ENDPOINT +
                "?grant_type=authorization_code" +
                "&client_id=" + KAKAO_CLIENT_ID +
                "&client_secret=" + KAKAO_CLIENT_SECRET +
                "&redirect_uri=" + KAKAO_REDIRECT_URI +
                "&code=" + code, null, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        return rootNode.get("access_token").asText();
    }
//    private Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
    private static final String MSG_SEND_SERVICE_URL = "https://kapi.kakao.com/v2/api/talk/memo/default/send";
    private static final String SEND_SUCCESS_MSG = "메시지 전송에 성공했습니다.";
    private static final String SEND_FAIL_MSG = "메시지 전송에 실패했습니다.";

    private static final String SUCCESS_CODE = "0"; //kakao api에서 return해주는 success code 값

    public boolean sendMessage(String accessToken, DefaultMessageDto msgDto) {
        try {
            JSONObject linkObj = new JSONObject();
            linkObj.put("web_url", msgDto.webUrl());
            linkObj.put("mobile_web_url", msgDto.mobileUrl());

            JSONObject templateObj = new JSONObject();
            templateObj.put("object_type", msgDto.objType());
            templateObj.put("text", msgDto.text());
            templateObj.put("link", linkObj);
            templateObj.put("button_title", msgDto.btnTitle());

            HttpHeaders header = new HttpHeaders();
            header.set("Content-Type", APP_TYPE_URL_ENCODED);
            header.set("Authorization", "Bearer " + accessToken);

            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add("template_object", templateObj.toString());

            HttpEntity<?> messageRequestEntity = kakaoMessage.httpClientEntity(header, parameters);

            ResponseEntity<String> response = kakaoMessage.httpRequest(MSG_SEND_SERVICE_URL, HttpMethod.POST, messageRequestEntity);


            String resultCode = "";
            JSONObject jsonData = new JSONObject(response.getBody());
            resultCode = jsonData.get("result_code").toString();

            return successCheck(resultCode);
        } catch (Exception e) {
            // 예외 처리
            e.printStackTrace();
            return false;
        }
    }

    public boolean successCheck(String resultCode) {
        if(resultCode.equals(SUCCESS_CODE)) {
//            logger.info(SEND_SUCCESS_MSG);
            return true;
        }else {
//            logger.debug(SEND_FAIL_MSG);
            return false;
        }

    }

    private static final String FRIEND_MESSAGE_SEND_URL = "https://kapi.kakao.com/v1/api/talk/friends/message/default/send";

    public boolean sendMessageToFriend(String accessToken, DefaultMessageDto msgDto, String receiverUuid) {
        try {
            JSONObject templateObj = createTemplateObject(msgDto);

            HttpHeaders header = new HttpHeaders();
            header.set("Content-Type", APP_TYPE_URL_ENCODED);
            header.set("Authorization", "Bearer " + accessToken);

            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add("receiver_uuids", "[\"" + receiverUuid + "\"]");
            parameters.add("template_object", templateObj.toString());

            HttpEntity<?> messageRequestEntity = kakaoMessage.httpClientEntity(header, parameters);

            ResponseEntity<String> response = kakaoMessage.httpRequest(FRIEND_MESSAGE_SEND_URL, HttpMethod.POST, messageRequestEntity);

            JSONObject jsonData = new JSONObject(response.getBody());

            if (jsonData.has("successful_receiver_uuids")) {
                JSONArray successfulReceivers = jsonData.getJSONArray("successful_receiver_uuids");
                System.out.println("메시지 전송 성공한 사용자: " + successfulReceivers.toString());
            }

            if (jsonData.has("failure_info")) {
                JSONArray failureInfo = jsonData.getJSONArray("failure_info");
                System.out.println("메시지 전송 실패 정보: " + failureInfo.toString());
            }

            return !jsonData.has("failure_info") || jsonData.getJSONArray("failure_info").length() == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private JSONObject createTemplateObject(DefaultMessageDto msgDto) {
        JSONObject linkObj = new JSONObject();
        linkObj.put("web_url", msgDto.webUrl());
        linkObj.put("mobile_web_url", msgDto.mobileUrl());

        JSONObject templateObj = new JSONObject();
        templateObj.put("object_type", msgDto.objType());
        templateObj.put("text", msgDto.text());
        templateObj.put("link", linkObj);
        templateObj.put("button_title", msgDto.btnTitle());

        return templateObj;
    }
    private static final String FRIEND_LIST_URL = "https://kapi.kakao.com/v1/api/talk/friends";

    public Optional<Friend> getFriendByNickname(String accessToken, String nickname) {
        List<Friend> allFriends = getAllFriends(accessToken);
        return allFriends.stream()
                .filter(friend -> friend.getProfile_nickname().equals(nickname))
                .findFirst();
    }

    public List<Friend> getAllFriends(String accessToken) {
        List<Friend> allFriends = new ArrayList<>();
        String nextUrl = "https://kapi.kakao.com/v1/api/talk/friends";

        while (nextUrl != null) {
            ResponseEntity<KakaoFriendsResponse> response = makeApiCall(accessToken, nextUrl);
            KakaoFriendsResponse friendsResponse = response.getBody();

            if (friendsResponse != null) {
                allFriends.addAll(friendsResponse.getElements());
                nextUrl = friendsResponse.getAfter_url();
            } else {
                nextUrl = null;
            }
        }

        return allFriends;
    }

    private ResponseEntity<KakaoFriendsResponse> makeApiCall(String accessToken, String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                KakaoFriendsResponse.class
        );
    }

    // Friend 클래스 정의

}
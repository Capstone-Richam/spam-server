package com.Nunbody.jwt;

import com.Nunbody.domain.member.domain.Member;
import com.Nunbody.token.Token;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {
private final ObjectMapper objectMapper;
    public Token getAccessToken(ResponseEntity<String> response) throws JsonProcessingException {

        return objectMapper.readValue(response.getBody(), Token.class);
    }
    public Member getUserInfo(ResponseEntity<String> userInfoResponse) throws JsonProcessingException{


        return objectMapper.readValue(userInfoResponse.getBody(), Member.class);
    }
}

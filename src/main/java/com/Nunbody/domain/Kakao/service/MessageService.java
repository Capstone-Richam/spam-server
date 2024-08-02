package com.Nunbody.domain.Kakao.service;

import com.Nunbody.domain.Kakao.service.dto.DefaultMessageDto;
import com.Nunbody.domain.Kakao.service.dto.Friend;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final KakaoService kakaoService;

    public boolean sendMyMessage(String code) throws JsonProcessingException {
        String accessToken = kakaoService.getAuthToken(code);

        DefaultMessageDto myMsg = DefaultMessageDto.of("text", "emap 테스트","https://www.richam.site/login","https://www.richam.site/","자세히보기");

        return kakaoService.sendMessage(accessToken, myMsg);
    }

    public boolean sendFriend(String code) throws JsonProcessingException {
        String accessToken = kakaoService.getAuthToken(code);

        DefaultMessageDto myMsg = DefaultMessageDto.of("text", "emap 테스트","https://www.richam.site/login","https://www.richam.site/","자세히보기");
        Optional<Friend> users =  kakaoService.getFriendByNickname(accessToken,"성태현");
        return kakaoService.sendMessageToFriend(accessToken, myMsg, users.get().getUuid());
    }
}

package com.Nunbody.domain.Kakao.service;

import com.Nunbody.domain.Kakao.service.dto.DefaultMessageDto;
import com.Nunbody.domain.Kakao.service.dto.Friend;
import com.Nunbody.domain.Mail.service.MailService;
import com.Nunbody.domain.member.domain.Member;
import com.Nunbody.domain.member.service.MemberReader;
import com.Nunbody.external.WindyfloMailClient;
import com.Nunbody.global.common.EncoderDecoder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.mail.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final KakaoService kakaoService;
    private final WindyfloMailClient windyfloMailClient;
    private final MemberReader memberReader;
    private final MailService mailService;
    private final MailScheduleService mailScheduleService;
    @Transactional
    public boolean sendMyMessage(Long memberId, String code) throws IOException, MessagingException {
        String accessToken = kakaoService.getAuthToken(code);

        Member member = memberReader.getMemberById(memberId);
        member.updateAccessToken(accessToken);
        Store store = mailService.connectToMailStore("imap.naver.com", member.getNaverId(), EncoderDecoder.decodeFromBase64(member.getNaverPassword()));

        Folder folder = store.getFolder("inbox");

        folder.open(Folder.READ_ONLY);

        int lastMessageIndex = folder.getMessageCount(); // 가장 최근 메일의 인덱스
        Message message = folder.getMessage(lastMessageIndex);
        Object object = message.getContent();

        String st = "";
        if (object instanceof String) {
            st = (String) object;
        } else if (object instanceof Multipart) {
            st = mailScheduleService.getTextFromMultipart((Multipart) message.getContent());
        }


        JsonNode jsonNode = windyfloMailClient.summaryMail(st);
        DefaultMessageDto myMsg = DefaultMessageDto.of("text", jsonNode.get("text").asText() +" 이메일이 도착했어요 확인해주세요","https://www.richam.site/login","https://www.richam.site/","메일 확인하러가기");

        return kakaoService.sendMessage(accessToken, myMsg);
    }

    public boolean sendFriend(String code) throws JsonProcessingException {
        String accessToken = kakaoService.getAuthToken(code);

        DefaultMessageDto myMsg = DefaultMessageDto.of("text", "emap 테스트","https://www.richam.site/login","https://www.richam.site/","자세히보기");
        Optional<Friend> users =  kakaoService.getFriendByNickname(accessToken,"성태현");
        return kakaoService.sendMessageToFriend(accessToken, myMsg, users.get().getUuid());
    }
}

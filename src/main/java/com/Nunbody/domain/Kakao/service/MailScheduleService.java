package com.Nunbody.domain.Kakao.service;

import com.Nunbody.domain.Kakao.service.dto.DefaultMessageDto;
import com.Nunbody.domain.Mail.domain.MailHeader;
import com.Nunbody.domain.Mail.domain.PlatformType;
import com.Nunbody.domain.Mail.repository.MailRepository;
import com.Nunbody.domain.Mail.service.MailService;
import com.Nunbody.domain.member.domain.Member;
import com.Nunbody.domain.member.repository.MemberRepository;
import com.Nunbody.domain.member.service.MemberReader;
import com.Nunbody.external.WindyfloMailClient;
import com.Nunbody.global.common.EncoderDecoder;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.mail.*;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.Nunbody.domain.Mail.domain.PlatformType.NAVER;

@Service
@RequiredArgsConstructor
public class MailScheduleService {

    private final MailService mailService;
    private final MemberReader memberReader;
    private final MailRepository mailRepository;
    private final WindyfloMailClient windyfloMailClient;
    private final KakaoService kakaoService;
    private final MemberRepository memberRepository;

    @Transactional
    public List<String> checkNewMailsAndGetContent(Member member, String platformHost, String platformId, String platformPassword, PlatformType platformType) {
        List<String> newMailContents = new ArrayList<>();

        try (Store store = mailService.connectToMailStore(platformHost, platformId, platformPassword);
             Folder folder = store.getFolder("inbox")) {

            folder.open(Folder.READ_ONLY);
            int lastMessageIndex = folder.getMessageCount(); // 가장 최근 메일의 인덱스
            Message message = folder.getMessage(lastMessageIndex);
            Object object = message.getContent();

            String st = "";
            if (object instanceof String) {
                st = (String) object;
            } else if (object instanceof Multipart) {
                st = getTextFromMultipart((Multipart) message.getContent());
            }
            Date latestStoredDate = member.getLastTime();
//                    mailRepository.findFirstByMemberIdAndPlatformTypeOrderByDateDesc(member.getId(), platformType)
//                    .map(MailHeader::getDate)
//                    .orElse(String.valueOf(new Date(0))); // 저장된 메일이 없으면 1970년 1월 1일로 설정

            Date messageDate = message.getReceivedDate();
            if ((messageDate.compareTo(latestStoredDate) > 0)) {
                st = getMessageContent(message);
                member.updateLastTime(messageDate);
                memberRepository.save(member);
                JsonNode jsonNode = windyfloMailClient.summaryMail(st);
                DefaultMessageDto myMsg = DefaultMessageDto.of("text", jsonNode.get("text").asText() +" 이메일이 도착했어요 확인해주세요","https://www.richam.site/login","https://www.richam.site/","메일 확인하러가기");

                kakaoService.sendMessage(member.getAccessToken(), myMsg);
                }
            else{
                return null;
            }
//            JsonNode jsonNode = windyfloMailClient.summaryMail(st);
//            DefaultMessageDto myMsg = DefaultMessageDto.of("text", jsonNode.get("text").asText() +" 이메일이 도착했어요 확인해주세요","https://www.richam.site/login","https://www.richam.site/","메일 확인하러가기");
//
//            kakaoService.sendMessage(member.getAccessToken(), myMsg);

        } catch (Exception e) {
//            log.error("Error in checkNewMailsAndGetContent for user: " + userId, e);
        }

        return newMailContents;
    }
    private Date parseDate(String dateStr) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return formatter.parse(dateStr);
        } catch (ParseException e) {
//            log.error("Error parsing date: " + dateStr, e);
            return new Date(0); // 파싱 실패 시 1970년 1월 1일 반환
        }
    }
    private String getMessageContent(Message message) throws MessagingException, IOException {
        Object content = message.getContent();
        if (content instanceof String) {
            return (String) content;
        } else if (content instanceof Multipart) {
            return getTextFromMultipart((Multipart) content);
        }
        return "Unable to extract content";
    }

    public String getTextFromMultipart(Multipart multipart) throws MessagingException, IOException {
        StringBuilder result = new StringBuilder();
        int count = multipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent());
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result.append(Jsoup.parse(html).text());
            } else if (bodyPart.getContent() instanceof Multipart) {
                result.append(getTextFromMultipart((Multipart) bodyPart.getContent()));
            }
        }
        return result.toString();
    }

    @Scheduled(fixedDelay = 10000) // 10000 milliseconds = 10 seconds
    public void scheduleMailCheck() {
        List<Member> users = memberReader.findAll();
        for (Member member : users) {
//        Member member = memberReader.getMemberById(8L);
            if (member.getAccessToken()!=null) {
                try {

                    List<String> newMailContents = checkNewMailsAndGetContent(
                            member,
                            "imap.naver.com",
                            member.getNaverId(),
                            EncoderDecoder.decodeFromBase64(member.getNaverPassword()),
                            PlatformType.NAVER
                    );
                    String body = newMailContents.get(0);

                    if (!newMailContents.isEmpty()) {
//                    log.info("New mails for user {}: {}", member.getId(), newMailContents.size());
                        // 여기서 새 메일 내용을 처리할 수 있습니다.
                        for (String content : newMailContents) {
//                        log.info("New mail content: {}", content.substring(0, Math.min(content.length(), 100)) + "...");
                        }
                    }
                } catch (Exception e) {
//                log.error("Failed to check mails for user: " + member.getId(), e);
                }
            }
        }
    }
}

package com.Nunbody.domain.Mail.service;

import com.Nunbody.domain.Mail.domain.MailBody;
import com.Nunbody.domain.Mail.domain.MailHeader;
import com.Nunbody.domain.Mail.domain.MailList;
import com.Nunbody.domain.Mail.domain.PlatformType;
import com.Nunbody.domain.Mail.dto.response.MailDetailResponseDto;
import com.Nunbody.domain.Mail.dto.response.MailResponseDto;
import com.Nunbody.domain.Mail.repository.MailBodyRepository;
import com.Nunbody.domain.Mail.repository.MailRepository;
import com.Nunbody.domain.member.domain.Member;
import com.Nunbody.domain.member.repository.MemberRepository;
import com.Nunbody.global.common.EncoderDecoder;
import com.sun.mail.util.BASE64DecoderStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.MimeMultipart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.*;
import java.util.Properties;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MailService {
    private final MongoTemplate mongoTemplate;
    private final MailBodyRepository mailBodyRepository;
    private final MailRepository mailRepository;
    private final MemberRepository memberRepository;
    private final Pattern pattern = Pattern.compile("<(.*?)>");
    private Matcher matcher;


    public MailList getNaverMail(Long memberId){

        MailList naverMail = MailList.builder()
                .memberId(memberId)
                .build();

        Member member = memberRepository.findById(memberId).orElse(null);
        String id = member.getNaverId();
        String decode  = EncoderDecoder.decodeFromBase64(member.getNaverPassword());

        /** naver mail */
        final String naverHost = "imap.naver.com";

        final String naverId = id;

        final String naverPassword = decode;

        naverMail = mailSetting(memberId,naverHost,naverId,naverPassword,naverMail,PlatformType.NAVER);
        return naverMail;

    }
    public MailList getGoogleMail(Long memberId){

        MailList googleMail = MailList.builder()
                .memberId(memberId)
                .build();
        Member member = memberRepository.findById(memberId).orElse(null);
        String id = member.getGmailId();
        String decode  = EncoderDecoder.decodeFromBase64(member.getGmailPassword());

        /** google mail */
        final String googleHost = "imap.gmail.com";

        final String googleId = id;

        final String googlePassword = decode;

        googleMail = mailSetting(memberId, googleHost, googleId, googlePassword ,googleMail, PlatformType.GOOGLE);
        return googleMail;

    }
    /*public MailList getMail(Long memberId){
        MailList naverMail = MailList.builder()
                .memberId(memberId)
                .build();

        MailList googleMail = MailList.builder()
                .memberId(memberId)
                .build();

    }*/
    public MailList mailSetting(Long userId, String platformHost, String platformId, String platformPassword, MailList mailList,
                                PlatformType platformType){
        List<MailBody> mailBodies = new ArrayList<>();

        try {
            Properties prop = new Properties();
            prop.put("mail.imap.host", platformHost);
            prop.put("mail.imap.port", 993);
            prop.put("mail.imap.ssl.enable", "true");
            prop.put("mail.imap.ssl.protocols", "TLSv1.2");
            prop.put("mail.store.protocol", "imap");

            // Session 클래스 인스턴스 생성
            Session session = Session.getInstance(prop);

            // Store 클래스
            Store store = session.getStore("imap");
            store.connect(platformHost,platformId,platformPassword);

            // 받은 편지함
            Folder folder = store.getFolder("inbox");
            folder.open(Folder.READ_ONLY);

            Message[] messages = folder.getMessages();
            MailHeader mailHeaderData;
            MailHeader latestMail = mailRepository.findFirstByMemberIdAndPlatformTypeOrderByDateDesc(userId,platformType).orElse(null);

            if (latestMail==null){
                reset(messages,userId, platformType,platformHost);
            }
            else {
                for (int i = messages.length - 2; i < messages.length; i++) {
                    matcher = pattern.matcher(messages[i].getFrom()[0].toString());
                    Instant receivedInstant = messages[i].getReceivedDate().toInstant();
                    ZonedDateTime kstDateTime = ZonedDateTime.ofInstant(receivedInstant, ZoneId.of("Asia/Seoul"));
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = kstDateTime.format(formatter);
                    int compare = formattedDate.compareTo(latestMail.getDate());
                    if (compare > 0) {
                        if (matcher.find()) {
                            String fromPerson = matcher.group(1);
                            mailHeaderData = MailHeader.builder()
                                    .title(messages[i].getSubject())
                                    .fromPerson(fromPerson)
                                    .date(formattedDate)
                                    .member(memberRepository.findById(userId).get())
                                    .platformType(platformType)
                                    .build();
                        } else {
                            mailHeaderData = MailHeader.builder()
                                    .title(messages[i].getSubject())
                                    .fromPerson(messages[i].getFrom()[0].toString())
                                    .date(formattedDate)
                                    .member(memberRepository.findById(userId).get())
                                    .platformType(platformType)
                                    .build();
                        }
                        mailRepository.save(mailHeaderData);
                        //mailList.addData(mailHeaderData);

                        Long mailId = mailHeaderData.getId();
                        mailBodies.add(extractMailBody(platformHost, messages[i], mailId));
                    }
                    mongoTemplate.insertAll(mailBodies);
                }
            }
            // 폴더와 스토어 닫기
            folder.close(false);
            store.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return mailList;
    }
    public void reset(Message[] messages, Long userId, PlatformType platformType, String platformHost)
            throws MessagingException, IOException {
        MailHeader mailHeaderData;
        List<MailBody> mailBodies = new ArrayList<>();
        for (int i = messages.length-30; i < messages.length; i++) {
            matcher = pattern.matcher(messages[i].getFrom()[0].toString());
            Instant receivedInstant = messages[i].getReceivedDate().toInstant();
            ZonedDateTime kstDateTime = ZonedDateTime.ofInstant(receivedInstant, ZoneId.of("Asia/Seoul"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDate = kstDateTime.format(formatter);
            if (matcher.find()) {
                String fromPerson = matcher.group(1);
                mailHeaderData = MailHeader.builder()
                        .title(messages[i].getSubject())
                        .fromPerson(fromPerson)
                        .date(formattedDate)
                        .member(memberRepository.findById(userId).get())
                        .platformType(platformType)
                        .build();
            } else {
                mailHeaderData = MailHeader.builder()
                        .title(messages[i].getSubject())
                        .fromPerson(messages[i].getFrom()[0].toString())
                        .date(formattedDate)
                        .member(memberRepository.findById(userId).get())
                        .platformType(platformType)
                        .build();
            }
            mailRepository.save(mailHeaderData);
            //mailList.addData(mailHeaderData);

            Long mailId = mailHeaderData.getId();
            mailBodies.add(extractMailBody(platformHost, messages[i], mailId));
        }
        mongoTemplate.insertAll(mailBodies);
    }

    public MailBody extractMailBody(String platformhost,Message messages, Long mailId) throws MessagingException, IOException {

        Object content = messages.getContent();
        byte[] contentBytes = null;
        if(platformhost.equals("imap.naver.com")) {
            if (content instanceof Multipart) {
                List<byte[]> multipartContentBytes = parseMultipart(messages, (Multipart) content);
                // 여러 BodyPart의 결과를 합쳐서 contentBytes로 설정
                contentBytes = combineMultipartContent(multipartContentBytes);
            } else if (content instanceof Part) {
                contentBytes = parseBody(messages, (BodyPart) content);
            }
        }
        if(platformhost.equals("imap.gmail.com")) {
            if (content instanceof Multipart) {
                //List<byte[]> multipartContentBytes = parseMultipart(messages, (Multipart) content);
                // 여러 BodyPart의 결과를 합쳐서 contentBytes로 설정
                contentBytes = null;
            } else
                if (content instanceof Part) {
                contentBytes = parseBody(messages, (BodyPart) content);
            }
        }
        MailBody mailBody = MailBody.builder()
                .mailId(mailId)
                .content(contentBytes != null ? new String(contentBytes, StandardCharsets.UTF_8) : " ")
                .build();

        return mailBody;
    }
    private static List<byte[]> parseMultipart(Message message, Multipart mp) throws IOException, MessagingException {
        MimeMultipart mm = (MimeMultipart) mp;

        List<byte[]> multipartContentBytes = new ArrayList<>();

        int bodyCount = mm.getCount();
        for (int i = 0; i < bodyCount; i++) {
            BodyPart bodyPart = mm.getBodyPart(i);
            Object partContent = bodyPart.getContent();

            byte[] partContentBytes = parseBody(message,bodyPart);
            multipartContentBytes.add(partContentBytes);
        }

        return multipartContentBytes;
    }
    private static byte[] combineMultipartContent(List<byte[]> multipartContentBytes) {
        // 여러 BodyPart의 결과를 합치는 로직을 구현
        // 예를 들어, 각 부분을 줄바꿈으로 구분하여 이어붙일 수 있습니다.
        StringBuilder combinedContent = new StringBuilder();
        for (byte[] partContentBytes : multipartContentBytes) {
            if (partContentBytes != null) {
                combinedContent.append(new String(partContentBytes, StandardCharsets.UTF_8));
                combinedContent.append(System.lineSeparator()); // 각 부분을 줄바꿈으로 구분
            }
        }

        // 최종 결과를 byte 배열로 변환
        return combinedContent.toString().getBytes(StandardCharsets.UTF_8);
    }
    private static byte[] parseBody(Message message, BodyPart bp) throws IOException, MessagingException {
        Object obj = bp.getContent();
        byte[] contentBytes = null;

        if (obj instanceof BASE64DecoderStream) {
            // 처리할 첨부 파일
            BASE64DecoderStream newObj = (BASE64DecoderStream) obj;

            contentBytes = readInputStream(newObj);

            newObj.close();
        } else if (obj instanceof String) {
            // 텍스트 형식인 경우
            String contentType = message.getContentType().toLowerCase();

            contentBytes = ((String) obj).getBytes(StandardCharsets.UTF_8);

        } else if (obj instanceof Multipart) {
            parseMultipart(message, (Multipart) obj);
        } else {
            // 기타 형식인 경우
            contentBytes = bp.getContentType().getBytes(StandardCharsets.UTF_8);
        }

        return contentBytes;
    }

    private static byte[] readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    public MailDetailResponseDto getMailBody(Long mailId) {
        MailBody mailBody = mailBodyRepository.findByMailId(mailId);
        MailResponseDto mailResponseDto = createMailDetailResponseDto(mailId);
        return MailDetailResponseDto.of(mailResponseDto, mailBody.getContent());
    }
    private MailResponseDto createMailDetailResponseDto(Long mailId){
        MailHeader mailHeader = getMailHeader(mailId);
        return MailResponseDto.of(mailHeader);
    }
    private MailHeader getMailHeader(Long mailId){
        return mailRepository.findById(mailId).orElse(null);
    }
}
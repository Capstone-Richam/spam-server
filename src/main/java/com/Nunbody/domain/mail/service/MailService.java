package com.Nunbody.domain.Mail.service;

import com.Nunbody.domain.Mail.domain.MailBody;
import com.Nunbody.domain.Mail.domain.MailHeader;
import com.Nunbody.domain.Mail.domain.MailList;
import com.Nunbody.domain.Mail.dto.response.MailBodyResponseDto;
import com.Nunbody.domain.Mail.repository.MailBodyRepository;
import com.Nunbody.domain.Mail.repository.MailRepository;
import com.Nunbody.domain.member.repository.MemberRepository;
import com.Nunbody.global.common.EncoderDecoder;
import com.sun.mail.util.BASE64DecoderStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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



    public MailList getMail(Long userId){
        List<MailBody> mailBodies = new ArrayList<>();
        MailList naverMail = MailList.builder()
                .userId(userId)
                .build();

        String id = memberRepository.findNaverIdById(userId).orElse(null);
        String decode  = EncoderDecoder.decodeFromBase64(memberRepository.findNaverPasswordById(userId).orElse(null));

        /** naver mail */
        final String naverHost = "imap.naver.com";

        final String naverId = id;

        final String naverPassword = decode;


        try {
            Properties prop = new Properties();
            prop.put("mail.imap.host", naverHost);
            prop.put("mail.imap.port", 993);
            prop.put("mail.imap.ssl.enable", "true");
            prop.put("mail.imap.ssl.protocols", "TLSv1.2");
            prop.put("mail.store.protocol", "imap");

            // Session 클래스 인스턴스 생성
            Session session = Session.getInstance(prop);

            // Store 클래스
            Store store = session.getStore("imap");
            store.connect(naverHost, naverId, naverPassword);

            // 받은 편지함
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            Message[] messages = folder.getMessages();
            MailHeader mailHeaderData;


            for (int i = 0; i < 20; i++) {
                matcher = pattern.matcher(messages[i].getFrom()[0].toString());
                if (matcher.find()) {
                    String fromPerson = matcher.group(1);
                    mailHeaderData = MailHeader.builder()
                            .title(messages[i].getSubject())
                            .fromPerson(fromPerson)
                            .date(String.valueOf(messages[i].getReceivedDate()))
                            .build();
                } else {
                    mailHeaderData = MailHeader.builder()
                            .title(messages[i].getSubject())
                            .fromPerson(messages[i].getFrom()[0].toString())
                            .build();
                }
                mailRepository.save(mailHeaderData);
                naverMail.addData(mailHeaderData);

                Long mailId = mailHeaderData.getId();
                mailBodies.add(extractMailBody(messages[i],mailId));
            }
            mongoTemplate.insertAll(mailBodies);
            // 폴더와 스토어 닫기
            folder.close(false);
            store.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return naverMail;
    }

    public MailBody extractMailBody(Message messages, Long mailId) throws MessagingException, IOException {

        Object content = messages.getContent();
        byte[] contentBytes = null;

        if (content instanceof Multipart) {
            List<byte[]> multipartContentBytes = parseMultipart(messages, (Multipart) content);
            // 여러 BodyPart의 결과를 합쳐서 contentBytes로 설정
            contentBytes = combineMultipartContent(multipartContentBytes);
        } else if(content instanceof Part){
            contentBytes = parseBody(messages, (BodyPart) content);
        }

        MailBody mailBody = MailBody.builder()
                .mailId(mailId)
                .content(contentBytes != null ? new String(contentBytes, StandardCharsets.UTF_8) : null)
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

    public MailBodyResponseDto getMailBody(Long mailId) {
        MailBody mailBody = mailBodyRepository.findByMailId(mailId);
        MailBodyResponseDto mailBodyResponseDto = MailBodyResponseDto.builder()
                .content(mailBody.getContent())
                .build();
        return mailBodyResponseDto;
    }
}
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

import jakarta.mail.*;
import jakarta.mail.internet.MimeUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.Nunbody.domain.Mail.domain.PlatformType.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService {
    private final MongoTemplate mongoTemplate;
    private final MailBodyRepository mailBodyRepository;
    private final MailRepository mailRepository;
    private final MemberRepository memberRepository;
    private final Pattern pattern = Pattern.compile("<(.*?)>");

    public void getMail(Long memberId, String type) {;
        PlatformType platformType =getEnumPlatformTypeFromStringPlatformType(type);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        String id, password, host;
        switch (platformType) {
            case NAVER:
                id = member.getNaverId();
                password = EncoderDecoder.decodeFromBase64(member.getNaverPassword());
                host = "imap.naver.com";
                break;
            case GOOGLE:
                id = member.getGmailId();
                password = EncoderDecoder.decodeFromBase64(member.getGmailPassword());
                host = "imap.gmail.com";
                break;
            default:
                throw new IllegalArgumentException("Unsupported platform type");
        }

        mailSetting(memberId, host, id, password, platformType);
    }

    @Transactional
    public MailList mailSetting(Long userId, String platformHost, String platformId, String platformPassword, PlatformType platformType) {
        List<MailBody> mailBodies = new ArrayList<>();
        MailList mailList = MailList.builder().memberId(userId).build();

        try (Store store = connectToMailStore(platformHost, platformId, platformPassword);
             Folder folder = store.getFolder("inbox")) {

            folder.open(Folder.READ_ONLY);

            Message[] messages = folder.getMessages();
            MailHeader latestMail = mailRepository.findFirstByMemberIdAndPlatformTypeOrderByDateDesc(userId, platformType).orElse(null);

            if (latestMail == null) {
                reset(messages, userId, platformType, platformHost);
            } else {
                processNewMails(messages, userId, platformType, platformHost, latestMail, mailBodies);
            }

            mongoTemplate.insertAll(mailBodies);

        } catch (Exception e) {
            log.error("Error in mailSetting", e);
            throw new RuntimeException("Failed to process emails", e);
        }

        return mailList;
    }

    public Store connectToMailStore(String host, String username, String password) throws MessagingException {
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");
        props.setProperty("mail.imaps.host", host);
        props.setProperty("mail.imaps.port", "993");
        props.setProperty("mail.imaps.ssl.enable", "true");
        props.setProperty("mail.imap.ssl.protocols", "TLSv1.2");
        props.setProperty("mail.imap.ssl.ciphersuites", "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256");

        Session session = Session.getInstance(props);
        Store store = session.getStore("imaps");
        store.connect(host, username, password);
        return store;
    }

    private void processNewMails(Message[] messages, Long userId, PlatformType platformType, String platformHost,
                                 MailHeader latestMail, List<MailBody> mailBodies) throws MessagingException, IOException {
        LocalDateTime latestMailDate = LocalDateTime.parse(latestMail.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ZoneId seoulZone = ZoneId.of("Asia/Seoul");

        for (int i = messages.length - 1; i >= 0; i--) {
            Message message = messages[i];
            LocalDateTime messageDate = LocalDateTime.ofInstant(message.getReceivedDate().toInstant(), seoulZone);

            if (messageDate.isAfter(latestMailDate)) {
                MailHeader mailHeader = createMailHeader(message, userId, platformType);
                mailRepository.save(mailHeader);
                mailBodies.add(extractMailBody(platformHost, message, mailHeader.getId()));
            } else {
                break;  // No need to check older messages
            }
        }
    }

    private MailHeader createMailHeader(Message message, Long userId, PlatformType platformType) throws MessagingException {
        Matcher matcher = pattern.matcher(message.getFrom()[0].toString());
        String fromPerson = matcher.find() ? matcher.group(1) : message.getFrom()[0].toString();

        ZonedDateTime kstDateTime = ZonedDateTime.ofInstant(message.getReceivedDate().toInstant(), ZoneId.of("Asia/Seoul"));
        String formattedDate = kstDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return MailHeader.builder()
                .title(message.getSubject())
                .fromPerson(fromPerson)
                .date(formattedDate)
                .member(memberRepository.getReferenceById(userId))
                .platformType(platformType)
                .build();
    }

    @Transactional
    public void reset(Message[] messages, Long userId, PlatformType platformType, String platformHost)
            throws MessagingException, IOException {
        List<MailBody> mailBodies = new ArrayList<>();
        int startIndex = Math.max(0, messages.length - 20);

        for (int i = startIndex; i < messages.length; i++) {
            MailHeader mailHeader = createMailHeader(messages[i], userId, platformType);
            mailRepository.save(mailHeader);
            mailBodies.add(extractMailBody(platformHost, messages[i], mailHeader.getId()));
        }
        mongoTemplate.insertAll(mailBodies);
    }

    public MailBody extractMailBody(String platformHost, Message message, Long mailId) {
        try {
            log.debug("Extracting mail body for mailId: {} on platform: {}", mailId, platformHost);

            Object content = message.getContent();
            String decodedContent = "";

            if (content instanceof Multipart) {
                decodedContent = handleMultipart((Multipart) content);
            } else if (content instanceof String) {
                decodedContent = MimeUtility.decodeText((String) content);
            } else if (content != null) {
                decodedContent = content.toString();
            }

            return MailBody.builder()
                    .mailId(mailId)
                    .content(decodedContent.isEmpty() ? "No content available" : decodedContent)
                    .build();

        } catch (MessagingException | IOException e) {
            log.error("Error extracting mail body for mailId: {} on platform: {}", mailId, platformHost, e);
            return MailBody.builder()
                    .mailId(mailId)
                    .content("Error occurred while extracting mail content")
                    .build();
        }
    }

    private String handleMultipart(Multipart multipart) throws MessagingException, IOException {
        StringBuilder result = new StringBuilder();
        int count = multipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append(MimeUtility.decodeText((String) bodyPart.getContent()));
            } else if (bodyPart.isMimeType("text/html")) {
                result.append(MimeUtility.decodeText((String) bodyPart.getContent()));
            }
        }
        return result.toString();
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

    @Transactional(readOnly = true)
    public MailDetailResponseDto getMailBody(Long mailId) {
        MailBody mailBody = mailBodyRepository.findByMailId(mailId);
        MailHeader mailHeader = mailRepository.findById(mailId)
                .orElseThrow(() -> new RuntimeException("Mail header not found"));
        MailResponseDto mailResponseDto = MailResponseDto.of(mailHeader);
        return MailDetailResponseDto.of(mailResponseDto, mailBody.getContent());
    }
}
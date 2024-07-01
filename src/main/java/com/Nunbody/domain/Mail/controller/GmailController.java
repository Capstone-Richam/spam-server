package com.Nunbody.domain.Mail.controller;

import com.Nunbody.domain.Mail.domain.MailBody;
import com.Nunbody.domain.Mail.domain.MailHeader;
import com.Nunbody.domain.Mail.domain.PlatformType;
import com.Nunbody.domain.Mail.repository.MailBodyRepository;
import com.Nunbody.domain.Mail.repository.MailRepository;
import com.Nunbody.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@RestController
public class GmailController {
    private final MailRepository mailRepository;
    private final MongoTemplate mongoTemplate;
    private static Long userId = Long.valueOf(1);
    private static PlatformType platformType = PlatformType.getEnumPlatformTypeFromStringPlatformType("GOOGLE");
    private static String platformHost ="imap.gmail.com";
    private final Pattern pattern = Pattern.compile("<(.*?)>");
    private final MemberRepository memberRepository;
    private final MailBodyRepository mailBodyRepository;
    public static List<MailBody> mailBodies = new ArrayList<>();
    @GetMapping("/gmail")
    public ResponseEntity<?> getGmailMessages() throws MessagingException, IOException {

//        MailList mailList = MailList.builder().memberId(userId).build();
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");
        props.setProperty("mail.imaps.host", "imap.gmail.com");
        props.setProperty("mail.imaps.port", "993");
        props.setProperty("mail.imaps.ssl.enable", "true");
        props.setProperty("mail.imap.ssl.protocols", "TLSv1.2");
        props.setProperty("mail.imap.ssl.ciphersuites", "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256");
        props.setProperty("mail.debug.ssl", "true");

        Session session = Session.getInstance(props);
        StringBuilder result = new StringBuilder();


            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", "qogustj50@gmail.com", "eqno csrt cmcm xnaa");


            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            Message[] messages = inbox.getMessages(1, 9); // 최근 10개 메일 가져오기

            MailHeader latestMail = mailRepository.findFirstByMemberIdAndPlatformTypeOrderByDateDesc(userId, platformType).orElse(null);

//            if (latestMail == null) {
//                reset(messages, userId, platformType, platformHost);
//            } else {
                processNewMails(messages, userId, platformType, platformHost, latestMail, mailBodies);
//            }

//            mongoTemplate.insertAll(mailBodies);
mailBodyRepository.saveAll(mailBodies);

        return ResponseEntity.ok(null);
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
    private void processNewMails(Message[] messages, Long userId, PlatformType platformType, String platformHost,
                                 MailHeader latestMail, List<MailBody> mailBodies) throws MessagingException, IOException {
//        LocalDateTime latestMailDate = LocalDateTime.parse(latestMail.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ZoneId seoulZone = ZoneId.of("Asia/Seoul");

        for (int i = messages.length - 1; i >= 0; i--) {
            Message message = messages[i];
            LocalDateTime messageDate = LocalDateTime.ofInstant(message.getReceivedDate().toInstant(), seoulZone);

//            if (!messageDate.isAfter(latestMailDate)) {
                MailHeader mailHeader = createMailHeader(message, userId, platformType);
                mailRepository.save(mailHeader);
                mailBodies.add(extractMailBody(platformHost, message, mailHeader.getId()));
//            } else {
//                break;  // No need to check older messages
//            }
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
    public MailBody extractMailBody(String platformHost, Message message, Long mailId) {
        try {
//            log.debug("Extracting mail body for mailId: {} on platform: {}", mailId, platformHost);

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
//            log.error("Error extracting mail body for mailId: {} on platform: {}", mailId, platformHost, e);
            return MailBody.builder()
                    .mailId(mailId)
                    .content("Error occurred while extracting mail content")
                    .build();
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
    private String getTextFromMessage(Message message) throws MessagingException, IOException {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            return getTextFromMimeMultipart(mimeMultipart);
        }
        return "";
    }

    private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append("\n").append(bodyPart.getContent());
                break; // 일반 텍스트 부분을 찾았으므로 중단
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result.append(getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent()));
            }
        }
        return result.toString();
    }
}

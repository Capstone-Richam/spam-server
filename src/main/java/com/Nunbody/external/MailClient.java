package com.Nunbody.external;

import com.Nunbody.domain.Mail.domain.PlatformType;
import com.Nunbody.domain.Mail.dto.resquest.EmailReqDto;
import com.Nunbody.domain.member.domain.Member;
import com.Nunbody.domain.member.service.MemberReader;
import com.Nunbody.global.common.EncoderDecoder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
@RequiredArgsConstructor
public class MailClient {
    private final MemberReader memberReader;

    public void sendMail(Long memberId, EmailReqDto emailReqDto) {
        Member member = memberReader.getMemberById(memberId);
        PlatformType platformType = PlatformType.getEnumPlatformTypeFromStringPlatformType(emailReqDto.platform());
        JavaMailSender mailSender = createMailSender(member, platformType);
        String senderMail = getSenderMail(member, platformType);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            message.setFrom(senderMail);
            message.setRecipients(MimeMessage.RecipientType.TO, emailReqDto.mail());
            message.setSubject(emailReqDto.header());
            message.setText(emailReqDto.body(), "UTF-8", "html");

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to create or send email message", e);
        }
    }

    private JavaMailSender createMailSender(Member member, PlatformType platformType) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        switch (platformType) {
            case NAVER:
                mailSender.setHost("smtp.naver.com");
                mailSender.setPort(587);
                mailSender.setUsername(member.getNaverId());
                mailSender.setPassword(EncoderDecoder.decodeFromBase64(member.getNaverPassword()));
                break;
            case GOOGLE:
                mailSender.setHost("smtp.gmail.com");
                mailSender.setPort(587);
                mailSender.setUsername(member.getGmailId());
                mailSender.setPassword(EncoderDecoder.decodeFromBase64(member.getGmailPassword()));
                break;
            default:
                throw new IllegalArgumentException("Unsupported platform type: " + platformType);
        }

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        props.put("mail.smtp.timeout", "5000");

        return mailSender;
    }

    private String getSenderMail(Member member, PlatformType platformType) {
        return switch (platformType) {
            case NAVER -> member.getNaverId();
            case GOOGLE -> member.getGmailId();
            default -> throw new IllegalArgumentException("Unsupported platform type: " + platformType);
        };
    }
}
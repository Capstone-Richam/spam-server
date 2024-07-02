package com.Nunbody.external;

import com.Nunbody.domain.Mail.dto.resquest.EmailReqDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class GmailClient {
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String senderEmail;

    public GmailClient(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public MimeMessage CreateMail(EmailReqDto emailReqDto) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, emailReqDto.mail());
            message.setSubject(emailReqDto.header());
            message.setText(emailReqDto.body(),"UTF-8", "html");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        return message;
    }
    public void send(MimeMessage message){
        javaMailSender.send(message);
    }
}

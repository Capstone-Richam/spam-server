package com.Nunbody.domain.Mail.service;

import com.Nunbody.domain.Mail.dto.resquest.EmailReqDto;
import com.Nunbody.external.GmailClient;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final GmailClient gmailClient;

    public void sendMail(EmailReqDto emailReqDto) {
        MimeMessage message = gmailClient.CreateMail(emailReqDto);
        gmailClient.send(message);
    }
}

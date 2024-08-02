package com.Nunbody.domain.Mail.service;

import com.Nunbody.domain.Mail.dto.resquest.EmailReqDto;
import com.Nunbody.external.MailClient;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final MailClient mailClient;

    public void sendMail(Long memberId, EmailReqDto emailReqDto) {
        mailClient.sendMail(memberId, emailReqDto);

    }
}

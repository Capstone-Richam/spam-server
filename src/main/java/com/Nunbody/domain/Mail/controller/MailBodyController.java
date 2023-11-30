package com.Nunbody.domain.Mail.controller;

import com.Nunbody.domain.Mail.domain.MailBody;
import com.Nunbody.domain.Mail.service.MailService;
import com.Nunbody.global.common.SuccessResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mail")
public class MailBodyController {
    @Autowired
    private MailService mailService;

    @GetMapping("{id}")
    public ResponseEntity<SuccessResponse<?>> getMailBody(@PathVariable("id") Long mailId){
        final MailBody mailBody = mailService.getMailBody(mailId);

        return SuccessResponse.ok(mailBody);
    }

}

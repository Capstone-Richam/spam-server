package com.Nunbody.domain.mail.controller;

import com.Nunbody.domain.mail.domain.MailBody;
import com.Nunbody.domain.mail.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MailBodyController {
    @Autowired
    private MailService mailService;
    @GetMapping("/mail_body")
    public MailBody test(){
        return mailService.Test();
    }
}

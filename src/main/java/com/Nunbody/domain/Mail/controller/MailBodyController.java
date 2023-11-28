package com.Nunbody.domain.Mail.controller;

import com.Nunbody.domain.Mail.domain.MailBody;
import com.Nunbody.domain.Mail.service.MailService;
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

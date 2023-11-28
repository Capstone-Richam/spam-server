package com.Nunbody.domain.Mail.controller;

import com.Nunbody.domain.Mail.domain.MailList;
import com.Nunbody.domain.Mail.service.MailService;
import com.Nunbody.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/mail")
public class MailController {
    private final MailService mailService;
    @GetMapping("/mails")
    @ResponseBody
    public ResponseEntity<SuccessResponse<?>> getMail(@RequestParam("host") String host) {
        final MailList mailList = mailService.getMail(host);
        return SuccessResponse.ok(mailList);
    }



}


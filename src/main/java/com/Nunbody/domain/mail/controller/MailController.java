package com.Nunbody.domain.Mail.controller;

import com.Nunbody.domain.Mail.domain.MailBody;
import com.Nunbody.domain.Mail.domain.MailList;
import com.Nunbody.domain.Mail.dto.response.MailBodyResponseDto;
import com.Nunbody.domain.Mail.dto.response.MailListResponseDto;
import com.Nunbody.domain.Mail.dto.resquest.ValidateRequestDto;
import com.Nunbody.domain.Mail.service.MailManageService;
import com.Nunbody.domain.Mail.service.MailService;
import com.Nunbody.global.common.SuccessResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/mail")
public class MailController {
    private final MailService mailService;
    private final MailManageService mailManageService;
    @GetMapping("/mails")
    public ResponseEntity<SuccessResponse<?>> getMail(@RequestParam Long memberId, @RequestParam String type) {
        MailList mailList;
        String platform = type;

        if(platform.equals("naver")) {
            mailList = mailService.getNaverMail(memberId);
        }
        else {
            mailList = mailService.getGoogleMail(memberId);
        }
        return SuccessResponse.ok(mailList);
    }
    @GetMapping("/header")
    public ResponseEntity<SuccessResponse<?>> getHeader(@RequestParam Long memberId, @PageableDefault Pageable pageable){
        final Page<MailListResponseDto> mailListResponseDtoList = mailManageService.getMailList(memberId, pageable);
        return SuccessResponse.ok(mailListResponseDtoList);
    }
    @PostMapping("/validate")
    public ResponseEntity<SuccessResponse<?>> validate(@RequestBody ValidateRequestDto validateRequestDto) throws MessagingException {
        final String string= mailManageService.validateConnect(validateRequestDto);
        return SuccessResponse.ok(string);
    }
    @GetMapping("/header/{id}")
    public ResponseEntity<SuccessResponse<?>> getMailBody(@PathVariable("id") Long mailId){
        final MailBodyResponseDto mailBody = mailService.getMailBody(mailId);

        return SuccessResponse.ok(mailBody);
    }
}


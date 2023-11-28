package com.Nunbody.domain.Mail.controller;

import com.Nunbody.domain.Mail.domain.MailList;
import com.Nunbody.domain.Mail.dto.response.MailListResponseDto;
import com.Nunbody.domain.Mail.service.MailManageService;
import com.Nunbody.domain.Mail.service.MailService;
import com.Nunbody.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/mail")
public class MailController {
    private final MailService mailService;
    private final MailManageService mailManageService;
    @GetMapping("/mails")
    @ResponseBody
    public ResponseEntity<SuccessResponse<?>> getMail(@RequestParam("host") String host) {
        final MailList mailList = mailService.getMail(host);
        return SuccessResponse.ok(mailList);
    }

    @GetMapping("/header")
    public ResponseEntity<SuccessResponse<?>> getHeader(@RequestParam Long userId, @PageableDefault Pageable pageable){
        final List<MailListResponseDto> mailListResponseDtoList = mailManageService.getMailList(userId, pageable);
        return SuccessResponse.ok(mailListResponseDtoList);
    }

}


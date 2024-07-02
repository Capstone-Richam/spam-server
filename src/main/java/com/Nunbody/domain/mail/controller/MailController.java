package com.Nunbody.domain.Mail.controller;

import com.Nunbody.domain.Mail.domain.MailList;
import com.Nunbody.domain.Mail.domain.PlatformType;
import com.Nunbody.domain.Mail.dto.response.MailDetailResponseDto;
import com.Nunbody.domain.Mail.dto.response.MailResponseDto;
import com.Nunbody.domain.Mail.dto.resquest.ValidateRequestDto;
import com.Nunbody.domain.Mail.service.MailManageService;
import com.Nunbody.domain.Mail.service.MailService;
import com.Nunbody.global.common.SuccessResponse;
import com.Nunbody.global.config.auth.MemberId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

import static com.Nunbody.domain.Mail.domain.PlatformType.NAVER;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/mail")
public class MailController {
    private final MailService mailService;
    private final MailManageService mailManageService;

    @GetMapping("/mails")
    public ResponseEntity<SuccessResponse<?>> getMail(@MemberId Long memberId, @RequestParam String type) {

            mailService.getMail(memberId,type);
        return SuccessResponse.ok(null);
    }

    @GetMapping("/header")
    public ResponseEntity<SuccessResponse<?>> getHeader(@MemberId Long memberId, @RequestParam String type, @PageableDefault Pageable pageable) {
        final Page<MailResponseDto> mailListResponseDtoList = mailManageService.getMailList(memberId, type, pageable);
        return SuccessResponse.ok(mailListResponseDtoList);
    }

    @PostMapping("/validate")
    public ResponseEntity<SuccessResponse<?>> validate(@RequestBody ValidateRequestDto validateRequestDto) throws MessagingException {
        final String string = mailManageService.validateConnect(validateRequestDto);
        return SuccessResponse.ok(string);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<SuccessResponse<?>> getMailBody(@PathVariable("id") Long mailId) {
        final MailDetailResponseDto mailBody = mailService.getMailBody(mailId);
        return SuccessResponse.ok(mailBody);
    }
}


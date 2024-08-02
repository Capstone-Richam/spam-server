package com.Nunbody.domain.Kakao.controller;

import com.Nunbody.domain.Kakao.service.MessageService;
import com.Nunbody.global.common.SuccessResponse;
import com.Nunbody.global.config.auth.MemberId;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/kakao")
public class KakaoController {
    private final MessageService messageService;
    @GetMapping("/send")
    public ResponseEntity<SuccessResponse<?>> sendMe(@RequestParam( name = "code") String code) throws JsonProcessingException {

        messageService.sendMyMessage(code);
        return SuccessResponse.ok(null);
    }
    @GetMapping("/send/friend")
    public ResponseEntity<SuccessResponse<?>> sendFriend(@RequestParam( name = "code") String code) throws JsonProcessingException {

        messageService.sendFriend(code);
        return SuccessResponse.ok(null);
    }
}

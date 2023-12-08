package com.Nunbody.domain.member.controller;


import com.Nunbody.domain.member.dto.MemberRegisterRequestDto;
import com.Nunbody.domain.member.dto.SignInRequestDto;
import com.Nunbody.domain.member.dto.SignInResponseDto;
import com.Nunbody.domain.member.service.MemberService;

import com.Nunbody.global.common.SuccessResponse;
import com.Nunbody.token.OAuthToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;


    @PostMapping("/signup")
    public ResponseEntity<SuccessResponse<?>> create(@RequestBody MemberRegisterRequestDto dto){
        memberService.register(dto);
        return SuccessResponse.ok(null);
    }

    @PostMapping("/signin")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<SuccessResponse<?>> signIn(@RequestBody SignInRequestDto dto) {
        SignInResponseDto signInResponseDto= memberService.signIn(dto.getAccount(), dto.getPassword());
        return SuccessResponse.ok(signInResponseDto);

    }

    @GetMapping("/code")
    public OAuthToken getOauthTokenWithCode(@PathVariable String socialLoginType, @RequestParam String code) throws JsonProcessingException {
        return memberService.getOauthTokenWithCode(socialLoginType,code);
    }

}

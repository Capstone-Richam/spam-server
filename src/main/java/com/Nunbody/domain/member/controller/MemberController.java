package com.Nunbody.domain.member.controller;


import com.Nunbody.domain.member.dto.MemberRegisterResponseDto;
import com.Nunbody.domain.member.dto.SignInRequestDto;
import com.Nunbody.domain.member.dto.SignInResponseDto;

import com.Nunbody.domain.member.service.MemberService;

import com.Nunbody.token.OAuthToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class MemberController {
    @Autowired
    private MemberService memberService;


    @PostMapping("/signUp")
    public void create(@RequestBody MemberRegisterResponseDto dto){

        memberService.register(dto);

    }

    @PostMapping("/signIn")
    @ResponseStatus(HttpStatus.OK)
    public SignInResponseDto signIn(@RequestBody SignInRequestDto dto) {
        return memberService.signIn(dto.getAccount(), dto.getPassword());

    }

    @GetMapping("/code")
    public OAuthToken getOauthTokenWithCode(@PathVariable String socialLoginType, @RequestParam String code) throws JsonProcessingException {
        return memberService.getOauthTokenWithCode(socialLoginType,code);
    }
}

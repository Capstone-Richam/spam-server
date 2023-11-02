package com.Nunbody.controller;


import com.Nunbody.dto.MemberRegisterResponseDto;
import com.Nunbody.dto.SignInRequestDto;
import com.Nunbody.dto.SignInResponseDto;

import com.Nunbody.service.MemberService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @PostMapping("/user")

    public void create(@RequestBody MemberRegisterResponseDto dto){

        memberService.register(dto);

    }

    @PostMapping("/signIn")
    @ResponseStatus(HttpStatus.OK)
    public SignInResponseDto signIn(@RequestBody SignInRequestDto dto) {
        return memberService.signIn(dto.getAccount(), dto.getPassword());

    }
}

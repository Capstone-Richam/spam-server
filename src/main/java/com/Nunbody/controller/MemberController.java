package com.Nunbody.controller;

import com.Nunbody.dto.MemberRegisterResponseDto;
import com.Nunbody.service.MemberService;
import com.Nunbody.domain.Member;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberController {
    private MemberService memberService;
    @PostMapping("/user")
    public MemberRegisterResponseDto create(@RequestBody Member resource){

        MemberRegisterResponseDto member = memberService.register(resource);

        return member;
    }
}

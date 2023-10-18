package com.Nunbody.controller;


import com.Nunbody.service.MemberService;
import com.Nunbody.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MemberController {
    @Autowired
    private MemberService memberService;
    @PostMapping("/user")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody Member resource){

        memberService.register(resource);

    }
}

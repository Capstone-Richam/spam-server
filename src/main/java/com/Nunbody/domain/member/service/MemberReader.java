package com.Nunbody.domain.member.service;

import com.Nunbody.domain.member.domain.Member;
import com.Nunbody.domain.member.repository.MemberRepository;
import com.Nunbody.global.error.ErrorCode;
import com.Nunbody.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberReader {
    private final MemberRepository memberRepository;
    public Member getMemberById(Long memberId){
        return memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND));
    }
    public List<Member> findAll(){
        return memberRepository.findAll();
    }
}

package com.Nunbody.domain.member.controller;

import com.Nunbody.domain.member.domain.Keyword;
import com.Nunbody.domain.member.dto.KeywordRequestDto;
import com.Nunbody.domain.member.service.KeywordService;
import com.Nunbody.global.common.SuccessResponse;
import java.util.List;

import com.Nunbody.global.config.auth.MemberId;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/keyword")
public class KeywordController {
    private final KeywordService keywordService;
    @GetMapping
    public ResponseEntity<SuccessResponse<?>> list(@MemberId Long memberId){
        List<String> words = keywordService.getKeyword(memberId);
        return SuccessResponse.ok(words);
    }
    @PatchMapping("")
    public ResponseEntity<SuccessResponse<?>> updateKeyword(@MemberId Long memberId, @RequestBody KeywordRequestDto keywordRequestDto){
        keywordService.addKeyword(memberId,keywordRequestDto);
        return SuccessResponse.ok(null);
    }
    @DeleteMapping("")
    public ResponseEntity<SuccessResponse<?>> deleteKeyword(@MemberId Long memberId, @RequestBody KeywordRequestDto keywordRequestDto){
        keywordService.deleteKeyword(memberId, keywordRequestDto);
        return SuccessResponse.ok(null);
    }
}

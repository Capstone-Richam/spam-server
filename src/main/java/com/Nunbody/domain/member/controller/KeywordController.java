package com.Nunbody.domain.member.controller;

import com.Nunbody.domain.member.dto.KeywordRequestDto;
import com.Nunbody.domain.member.service.KeywordService;
import com.Nunbody.global.common.SuccessResponse;
import com.Nunbody.global.config.auth.MemberId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/keyword")
public class KeywordController {
    private final KeywordService keywordService;

    @GetMapping
    public ResponseEntity<SuccessResponse<?>> list(@MemberId Long memberId) {
        List<String> words = keywordService.getKeyword(memberId);
        return SuccessResponse.ok(words);
    }

    @PatchMapping("")
    public ResponseEntity<SuccessResponse<?>> updateKeyword(@MemberId Long memberId, @RequestBody KeywordRequestDto keywordRequestDto) {
        keywordService.addKeyword(memberId, keywordRequestDto);
        return SuccessResponse.ok(null);
    }

    @PostMapping("")
    public ResponseEntity<SuccessResponse<?>> deleteKeyword(@MemberId Long memberId, @RequestBody KeywordRequestDto keywordRequestDto) {
        keywordService.deleteKeyword(memberId, keywordRequestDto);
        return SuccessResponse.ok(null);
    }
}

package com.Nunbody.domain.Mail.controller;

import com.Nunbody.domain.Mail.dto.response.FilterMailListResponseDto;
import com.Nunbody.domain.Mail.dto.resquest.FilterKeywordRequest;
import com.Nunbody.domain.Mail.service.FilterService;
import com.Nunbody.global.common.SuccessResponse;
import com.Nunbody.global.config.auth.MemberId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/filter")
public class MailFilterController {
    private final FilterService filterService;
    @PostMapping
    public ResponseEntity<SuccessResponse<?>> filtertest(@MemberId Long memberId, @RequestBody FilterKeywordRequest filterKeywordRequest, @PageableDefault Pageable pageable){
        Page<FilterMailListResponseDto> mailListResponseDtoList = filterService.filterContent(memberId,filterKeywordRequest,pageable);
        return SuccessResponse.ok(mailListResponseDtoList);
    }

    @PostMapping("/nlp")
    public ResponseEntity<SuccessResponse<?>> nlpFilter(@RequestParam Long memberId, @RequestBody FilterKeywordRequest filterKeywordRequest) throws IOException {
        List<String> list = filterService.categoryFilter(memberId, filterKeywordRequest);
        return SuccessResponse.ok(list);
    }
}

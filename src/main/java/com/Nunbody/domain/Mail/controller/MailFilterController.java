package com.Nunbody.domain.Mail.controller;

import com.Nunbody.domain.Mail.dto.response.MailListResponseDto;
import com.Nunbody.domain.Mail.dto.resquest.FilterKeywordRequest;
import com.Nunbody.domain.Mail.service.FilterService;
import com.Nunbody.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<SuccessResponse<?>> filtertest(@RequestBody FilterKeywordRequest filterKeywordRequest){
        List<MailListResponseDto> mailListResponseDtoList = filterService.filterContent(filterKeywordRequest);
        return SuccessResponse.ok(mailListResponseDtoList);
    }

    @PostMapping("/nlp")
    public ResponseEntity<SuccessResponse<?>> nlpFilter(@RequestBody FilterKeywordRequest filterKeywordRequest) throws IOException {
        List<String> list = filterService.categoryFilter(filterKeywordRequest);
        return SuccessResponse.ok(list);
    }
}

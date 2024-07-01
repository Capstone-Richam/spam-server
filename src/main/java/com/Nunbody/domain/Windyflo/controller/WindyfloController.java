package com.Nunbody.domain.Windyflo.controller;

import com.Nunbody.domain.Windyflo.dto.req.WindyfloReq;
import com.Nunbody.domain.Windyflo.service.WindyfloService;
import com.Nunbody.external.ConversationQARes;
import com.Nunbody.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/windyflo")
public class WindyfloController {
    private final WindyfloService windyfloService;
    @PostMapping
    public ResponseEntity<SuccessResponse<?>> createMail(@RequestBody WindyfloReq windyfloReq){
        ConversationQARes conversationQARes = windyfloService.createMail(windyfloReq);
        return SuccessResponse.ok(conversationQARes);
    }
}

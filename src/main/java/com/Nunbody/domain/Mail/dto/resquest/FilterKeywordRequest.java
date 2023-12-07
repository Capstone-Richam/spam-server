package com.Nunbody.domain.Mail.dto.resquest;

import lombok.Getter;

import java.util.List;

@Getter
public class FilterKeywordRequest {
    private Long memberId;
    private List<String> keywords;
}

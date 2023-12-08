package com.Nunbody.domain.Mail.dto.resquest;

import lombok.Getter;

import java.util.List;

@Getter
public class FilterKeywordRequest {
    private List<String> keywords;
}

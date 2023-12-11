package com.Nunbody.domain.member.dto;

import lombok.Data;

import java.util.List;

@Data
public class KeywordRequestDto {
    private List<String> words;
}

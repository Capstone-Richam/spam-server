package com.Nunbody.domain.member.dto;

import java.util.List;
import lombok.Data;

@Data
public class KeywordRequestDto {
    private Long memberId;
    private List<String> words;
}

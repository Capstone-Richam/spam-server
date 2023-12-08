package com.Nunbody.domain.Mail.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Builder
@Getter
public class MailDetailResponseDto {
    private MailResponseDto mailInfo;
    private String content;

    public static MailDetailResponseDto of(MailResponseDto mailResponseDto, String content){
        return MailDetailResponseDto.builder()
                .mailInfo(mailResponseDto)
                .content(content)
                .build();
    }
}

package com.Nunbody.domain.Mail.dto.response;

import com.Nunbody.domain.Mail.domain.MailHeader;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MailListResponseDto {
    private String title;
    private String fromPerson;
    private String date;

    public static MailListResponseDto of(MailHeader mailHeader){
        return MailListResponseDto.builder()
                .title(mailHeader.getTitle())
                .fromPerson(mailHeader.getFromPerson())
                .date(mailHeader.getDate())
                .build();
    }
}

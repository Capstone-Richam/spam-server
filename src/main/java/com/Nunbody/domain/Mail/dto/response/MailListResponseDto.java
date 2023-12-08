package com.Nunbody.domain.Mail.dto.response;

import com.Nunbody.domain.Mail.domain.MailHeader;
import com.Nunbody.domain.Mail.domain.PlatformType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MailListResponseDto {
    private Long mailId;
    private String title;
    private String fromPerson;
    private String date;
    private String type;

    public static MailListResponseDto of(MailHeader mailHeader){
        return MailListResponseDto.builder()
                .mailId(mailHeader.getId())
                .title(mailHeader.getTitle())
                .fromPerson(mailHeader.getFromPerson())
                .date(mailHeader.getDate())
                .type(mailHeader.getPlatformType().toString())
                .build();
    }
}

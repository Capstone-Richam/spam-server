package com.Nunbody.domain.Mail.dto.response;

import com.Nunbody.domain.Mail.domain.MailHeader;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FilterMailListResponseDto {
    private Long mailId;
    private String title;
    private String fromPerson;
    private String date;
    private String type;
    private String keyword;

    public static FilterMailListResponseDto of(MailHeader mailHeader,String keyword){
        return FilterMailListResponseDto.builder()
                .mailId(mailHeader.getId())
                .title(mailHeader.getTitle())
                .fromPerson(mailHeader.getFromPerson())
                .date(mailHeader.getDate())
                .type(mailHeader.getPlatformType().toString())
                .keyword(keyword)
                .build();
    }
}

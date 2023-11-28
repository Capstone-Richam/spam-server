package com.Nunbody.domain.Mail.dto.response;

import com.Nunbody.domain.Mail.domain.Mail;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MailListResponseDto {
    private String title;
    private String fromPerson;
    private String date;

    public static MailListResponseDto of(Mail mail){
        return MailListResponseDto.builder()
                .title(mail.getTitle())
                .fromPerson(mail.getFromPerson())
                .date(mail.getDate())
                .build();
    }
}

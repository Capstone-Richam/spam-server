package com.Nunbody.domain.Windyflo.dto.res;

public record EmailResDto(
        String template,
        String header,
        String body
) {
    public static EmailResDto of(String template, String header, String body){
        return new EmailResDto(template, header, body);
    }
}

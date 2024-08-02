package com.Nunbody.domain.Kakao.service.dto;

public record DefaultMessageDto(
        String objType,
        String text,
        String webUrl,
        String mobileUrl,
        String btnTitle
) {
    public static DefaultMessageDto of(String objType, String text, String webUrl, String mobileUrl, String btnTitle) {
        return new DefaultMessageDto(objType, text, webUrl, mobileUrl, btnTitle);
    }
}

package com.Nunbody.domain.member.dto.res;

public record MemberMailIdResDto(
        String GOOGLE,
        String NAVER
) {
    public static MemberMailIdResDto of(String GOOGLE, String NAVER){
        return new MemberMailIdResDto(GOOGLE, NAVER);
    }
}

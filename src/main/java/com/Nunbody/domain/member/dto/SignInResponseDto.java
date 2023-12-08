package com.Nunbody.domain.member.dto;


import com.Nunbody.domain.member.domain.Member;
import com.Nunbody.token.TokenInfo;
import lombok.*;



@Getter
@Builder
public class SignInResponseDto {
    private String naverId;
    private String gmailId;
    private String accessToken;
    private String refreshToken;

    public static SignInResponseDto of(Member member, TokenInfo tokenInfo){
        return SignInResponseDto.builder()
                .naverId(member.getNaverId())
                .gmailId(member.getGmailId())
                .accessToken(tokenInfo.getAccessToken())
                .refreshToken(member.getRefreshToken())
                .build();


    }
}

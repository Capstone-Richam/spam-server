package com.Nunbody.domain.member.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Data
public class SignInResponseDto {
    private Long memberId;
    private String account;
   // private String password;
    private String name;
    private String naverId;
    private String naverPassword;

    private String gmailId;
    private String gmailPassword;


    private String accessToken;
    private String refreshToken;
    private Long accessTokenRemainTime;
    private Long refreshTokenRemainTime;
}

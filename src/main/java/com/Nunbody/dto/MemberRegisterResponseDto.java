package com.Nunbody.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberRegisterResponseDto {
    private Long memberId;
    private String account;
    private String password;
    private String name;
    private String naverId;
    private String naverPassword;
    private boolean hasNaver;
    private String gmailId;
    private String gmailPassword;
    private boolean hasGmail;

}

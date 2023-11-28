package com.Nunbody.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberRegisterResponseDto {
    private Long id;
    private String account;
    private String password;
    private String name;
    private String naverId;
    private String naverPassword;
    private String gmailId;
    private String gmailPassword;

}

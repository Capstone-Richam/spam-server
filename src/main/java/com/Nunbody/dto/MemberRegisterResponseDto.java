package com.Nunbody.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberRegisterResponseDto {
    private String email;
    private String name;

}

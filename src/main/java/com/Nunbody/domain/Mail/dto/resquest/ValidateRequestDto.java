package com.Nunbody.domain.Mail.dto.resquest;

import lombok.Getter;

@Getter
public class ValidateRequestDto {
    private String type;
    private String id;
    private String password;
}

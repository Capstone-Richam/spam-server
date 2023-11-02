package com.Nunbody.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignInRequestDto {

    @NonNull
    private String account;
    @NonNull
    private String password;
}

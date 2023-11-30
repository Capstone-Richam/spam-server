package com.Nunbody.global.error.exception;

import com.Nunbody.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginException extends RuntimeException{
    private String message;
}

package com.Nunbody.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse<T> {
    private Boolean isSuccess;
    private int code;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    // 요청 성공
    public ErrorResponse(T result) {
        this.isSuccess = ErrorCode.SUCCESS.getIsSuccess();
        this.code = ErrorCode.SUCCESS.getCode();
        this.message = ErrorCode.SUCCESS.getMessage();
        this.result = result;
    }

    // 오류 발생
    public ErrorResponse(ErrorCode errorCode) {
        this.isSuccess = errorCode.getIsSuccess();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public ErrorResponse(ErrorCode errorCode, T result) {
        this.isSuccess = errorCode.getIsSuccess();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.result = result;
    }
}

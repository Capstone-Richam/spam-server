package com.Nunbody.global.error.exception;


import com.Nunbody.global.error.ErrorCode;

public class InvalidValueException extends BusinessException {
    public InvalidValueException() {
        super(ErrorCode.IMAP_ERROR);
    }

    public InvalidValueException(ErrorCode errorCode) {
        super(errorCode);
    }
}

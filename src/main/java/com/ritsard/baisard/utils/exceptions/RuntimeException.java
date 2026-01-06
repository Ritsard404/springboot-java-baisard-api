package com.ritsard.baisard.utils.exceptions;

import com.ritsard.baisard.utils.enums.ErrorCode;
import lombok.Getter;

@Getter
public class RuntimeException extends java.lang.RuntimeException {
    private final ErrorCode errorCode;

    // Message + ErrorCode
    public RuntimeException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    // Message + Cause + ErrorCode
    public RuntimeException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}

package com.ritsard.baisard.utils.exceptions;

import com.ritsard.baisard.utils.enums.ErrorCode;
import lombok.Getter;

@Getter
public class RuntimeException extends java.lang.RuntimeException {
    private ErrorCode errorCode;

    public RuntimeException(String message, ErrorCode errorCode){
        super(message);
        this.errorCode = errorCode;
    }
}

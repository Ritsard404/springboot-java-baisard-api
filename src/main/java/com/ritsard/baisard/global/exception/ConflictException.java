package com.ritsard.baisard.global.exception;

import com.ritsard.baisard.utils.enums.ErrorCode;
import com.ritsard.baisard.utils.exceptions.RuntimeException;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message, ErrorCode.CONFLICT_ERROR);
    }

    // Message + cause
    public ConflictException(String message, Throwable cause) {
        super(message, cause, ErrorCode.CONFLICT_ERROR);
    }
}

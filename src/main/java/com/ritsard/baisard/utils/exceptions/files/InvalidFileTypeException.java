package com.ritsard.baisard.utils.exceptions.files;

import com.ritsard.baisard.utils.enums.ErrorCode;

public class InvalidFileTypeException
extends RuntimeException {
    private final ErrorCode errorCode = ErrorCode.INVALID_FILE_TYPE;

    public InvalidFileTypeException() {
        super("\uc9c0\uc6d0\ud558\uc9c0 \uc54a\ub294 \ud30c\uc77c \ud615\uc2dd\uc785\ub2c8\ub2e4.");
    }

    public InvalidFileTypeException(String message) {
        super(message);
    }

    public InvalidFileTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorCode getErrorCode() {
        return this.errorCode;
    }
}


package com.ritsard.baisard.utils.exceptions;

import com.ritsard.baisard.utils.enums.ErrorCode;

public class NotFoundException extends RuntimeException {
    // Only message
    public NotFoundException(String message) {
        super(message, ErrorCode.NOT_FOUND_ERROR);
    }

    // Message + cause
    public NotFoundException(String message, Throwable cause) {
        super(message, cause, ErrorCode.NOT_FOUND_ERROR);
    }
}

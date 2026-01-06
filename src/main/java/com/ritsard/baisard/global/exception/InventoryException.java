package com.ritsard.baisard.global.exception;

import com.ritsard.baisard.utils.enums.ErrorCode;
import com.ritsard.baisard.utils.exceptions.RuntimeException;

public class InventoryException extends RuntimeException {
    public InventoryException(String message) {
        super(message, ErrorCode.VALIDATION_ERROR);
    }

    public InventoryException(String message, Throwable cause) {
        super(message, cause, ErrorCode.VALIDATION_ERROR);
    }
}
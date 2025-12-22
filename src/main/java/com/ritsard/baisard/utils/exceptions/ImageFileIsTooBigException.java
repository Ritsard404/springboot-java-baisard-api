/*
 * Decompiled with CFR 0.152.
 */
package com.ritsard.baisard.utils.exceptions;

import java.lang.RuntimeException;

public class ImageFileIsTooBigException
extends RuntimeException {
    public ImageFileIsTooBigException() {
        super("This image pixel is not valid");
    }

    public ImageFileIsTooBigException(String message) {
        super(message);
    }

    public ImageFileIsTooBigException(String message, Throwable cause) {
        super(message, cause);
    }
}


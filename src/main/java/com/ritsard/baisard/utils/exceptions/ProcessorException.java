/*
 * Decompiled with CFR 0.152.
 */
package com.ritsard.baisard.utils.exceptions;

import com.ritsard.baisard.utils.exceptions.gpt.GptException;

public class ProcessorException
        extends GptException {
    private final String processorName;

    public ProcessorException(String message, String processorName) {
        super(message);
        this.processorName = processorName;
    }

    public String getProcessorName() {
        return this.processorName;
    }
}


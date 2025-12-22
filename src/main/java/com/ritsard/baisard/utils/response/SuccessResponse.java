/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonCreator
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.ritsard.baisard.utils.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ritsard.baisard.utils.enums.SuccessCode;

public class SuccessResponse<T> {
    private T data;
    private int status;
    private String resultMsg;

    @JsonCreator
    public SuccessResponse(@JsonProperty(value = "data") T data, @JsonProperty(value = "status") int status, @JsonProperty(value = "resultMsg") String resultMsg) {
        this.data = data;
        this.status = status;
        this.resultMsg = resultMsg;
    }

    public SuccessResponse(SuccessCode successCode) {
        this.status = successCode.getStatus();
        this.resultMsg = successCode.getMessage();
    }

    public SuccessResponse(SuccessCode successCode, T result) {
        this.status = successCode.getStatus();
        this.resultMsg = successCode.getMessage();
        this.data = result;
    }

    public T getData() {
        return this.data;
    }

    public int getStatus() {
        return this.status;
    }

    public String getResultMsg() {
        return this.resultMsg;
    }

    public SuccessResponse() {
    }
}


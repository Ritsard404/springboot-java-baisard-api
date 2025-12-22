/*
 * Decompiled with CFR 0.152.
 */
package com.ritsard.baisard.utils.exceptions.gpt;

public class ContentFilteringException
        extends GptException {
    private final String filteredCategory;

    public ContentFilteringException(String message) {
        super(message);
        this.filteredCategory = "unknown";
    }

    public ContentFilteringException(String message, String filteredCategory) {
        super(message);
        this.filteredCategory = filteredCategory;
    }

    public ContentFilteringException(String message, String filteredCategory, Throwable cause) {
        super(message, cause);
        this.filteredCategory = filteredCategory;
    }

    public String getFilteredCategory() {
        return this.filteredCategory;
    }
}


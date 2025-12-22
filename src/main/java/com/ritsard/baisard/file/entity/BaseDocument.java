/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.persistence.Column
 *  jakarta.persistence.Entity
 *  jakarta.persistence.Table
 */
package com.ritsard.baisard.file.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="base_document")
public class BaseDocument
extends BaseFile {
    @Column(name="document_type")
    private String documentType;
    @Column(name="page_count")
    private Integer pageCount;
    @Column(name="is_text_extractable")
    private Boolean isTextExtractable;

    protected BaseDocument(BaseDocumentBuilder<?, ?> b) {
        super(b);
        this.documentType = b.documentType;
        this.pageCount = b.pageCount;
        this.isTextExtractable = b.isTextExtractable;
    }

    public static BaseDocumentBuilder<?, ?> builder() {
        return new BaseDocumentBuilderImpl();
    }

    public BaseDocumentBuilder<?, ?> toBuilder() {
        return new BaseDocumentBuilderImpl().$fillValuesFrom(this);
    }

    public String getDocumentType() {
        return this.documentType;
    }

    public Integer getPageCount() {
        return this.pageCount;
    }

    public Boolean getIsTextExtractable() {
        return this.isTextExtractable;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public void setIsTextExtractable(Boolean isTextExtractable) {
        this.isTextExtractable = isTextExtractable;
    }

    public BaseDocument() {
    }

    public BaseDocument(String documentType, Integer pageCount, Boolean isTextExtractable) {
        this.documentType = documentType;
        this.pageCount = pageCount;
        this.isTextExtractable = isTextExtractable;
    }

    public static abstract class BaseDocumentBuilder<C extends BaseDocument, B extends BaseDocumentBuilder<C, B>>
    extends BaseFile.BaseFileBuilder<C, B> {
        private String documentType;
        private Integer pageCount;
        private Boolean isTextExtractable;

        @Override
        protected B $fillValuesFrom(C instance) {
            super.$fillValuesFrom(instance);
            BaseDocumentBuilder.$fillValuesFromInstanceIntoBuilder(instance, this);
            return (B)((Object)this.self());
        }

        private static void $fillValuesFromInstanceIntoBuilder(BaseDocument instance, BaseDocumentBuilder<?, ?> b) {
            b.documentType(instance.documentType);
            b.pageCount(instance.pageCount);
            b.isTextExtractable(instance.isTextExtractable);
        }

        public B documentType(String documentType) {
            this.documentType = documentType;
            return (B)((Object)this.self());
        }

        public B pageCount(Integer pageCount) {
            this.pageCount = pageCount;
            return (B)((Object)this.self());
        }

        public B isTextExtractable(Boolean isTextExtractable) {
            this.isTextExtractable = isTextExtractable;
            return (B)((Object)this.self());
        }

        @Override
        protected abstract B self();

        @Override
        public abstract C build();

        @Override
        public String toString() {
            return "BaseDocument.BaseDocumentBuilder(super=" + super.toString() + ", documentType=" + this.documentType + ", pageCount=" + this.pageCount + ", isTextExtractable=" + this.isTextExtractable + ")";
        }
    }

    private static final class BaseDocumentBuilderImpl
    extends BaseDocumentBuilder<BaseDocument, BaseDocumentBuilderImpl> {
        private BaseDocumentBuilderImpl() {
        }

        @Override
        protected BaseDocumentBuilderImpl self() {
            return this;
        }

        @Override
        public BaseDocument build() {
            return new BaseDocument(this);
        }
    }
}


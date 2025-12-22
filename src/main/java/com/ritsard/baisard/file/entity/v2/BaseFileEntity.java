/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.lodong.basemodule.entity.BaseEntity
 *  com.lodong.basemodule.entity.BaseEntity$BaseEntityBuilder
 *  jakarta.persistence.MappedSuperclass
 */
package com.ritsard.baisard.file.entity.v2;

import com.ritsard.baisard.base.entity.BaseEntity;
import jakarta.persistence.MappedSuperclass;

import java.util.List;

@MappedSuperclass
public class BaseFileEntity<T extends FileInfo>
        extends BaseEntity {
    public void addFile(T file, List<T> fileList) {
        if (file == null || fileList == null) {
            throw new IllegalArgumentException("file\uc774\ub098 fileList\ub294 null\uc77c \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.");
        }
        fileList.add(file);
    }

    public void removeFile(T file, List<T> fileList) {
        if (file == null || fileList == null) {
            throw new IllegalArgumentException("file\uc774\ub098 fileList\ub294 null\uc77c \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.");
        }
        fileList.remove(file);
    }

    protected BaseFileEntity(BaseFileEntityBuilder<T, ?, ?> b) {
        super(b);
    }

    public static <T extends FileInfo> BaseFileEntityBuilder<T, ?, ?> builder() {
        return new BaseFileEntityBuilderImpl();
    }

    public BaseFileEntity() {
    }

    private static final class BaseFileEntityBuilderImpl<T extends FileInfo>
            extends BaseFileEntityBuilder<T, BaseFileEntity<T>, BaseFileEntityBuilderImpl<T>> {
        private BaseFileEntityBuilderImpl() {
        }

        @Override
        protected BaseFileEntityBuilderImpl<T> self() {
            return this;
        }

        @Override
        public BaseFileEntity<T> build() {
            return new BaseFileEntity(this);
        }
    }

    public static abstract class BaseFileEntityBuilder<T extends FileInfo, C extends BaseFileEntity<T>, B extends BaseFileEntityBuilder<T, C, B>>
            extends BaseEntity.BaseEntityBuilder<C, B> {
        protected abstract B self();

        public abstract C build();

        public String toString() {
            return "BaseFileEntity.BaseFileEntityBuilder(super=" + super.toString() + ")";
        }
    }
}


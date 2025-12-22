package com.ritsard.baisard.file.repository.v2;

import com.ritsard.baisard.base.repository.BaseSearchCondition;

public class BaseFileSearchCondition
        extends BaseSearchCondition {
    private String mimeType;
    private String fileExtension;
    private Long minFileSize;
    private Long maxFileSize;
    private Boolean isAttached;
    private String entityType;

    protected BaseFileSearchCondition(BaseFileSearchConditionBuilder<?, ?> b) {
        super(b);
        this.mimeType = b.mimeType;
        this.fileExtension = b.fileExtension;
        this.minFileSize = b.minFileSize;
        this.maxFileSize = b.maxFileSize;
        this.isAttached = b.isAttached;
        this.entityType = b.entityType;
    }

    public static BaseFileSearchConditionBuilder<?, ?> builder() {
        return new BaseFileSearchConditionBuilderImpl();
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public String getFileExtension() {
        return this.fileExtension;
    }

    public Long getMinFileSize() {
        return this.minFileSize;
    }

    public Long getMaxFileSize() {
        return this.maxFileSize;
    }

    public Boolean getIsAttached() {
        return this.isAttached;
    }

    public String getEntityType() {
        return this.entityType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public void setMinFileSize(Long minFileSize) {
        this.minFileSize = minFileSize;
    }

    public void setMaxFileSize(Long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public void setIsAttached(Boolean isAttached) {
        this.isAttached = isAttached;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String toString() {
        return "BaseFileSearchCondition(mimeType=" + this.getMimeType() + ", fileExtension=" + this.getFileExtension() + ", minFileSize=" + this.getMinFileSize() + ", maxFileSize=" + this.getMaxFileSize() + ", isAttached=" + this.getIsAttached() + ", entityType=" + this.getEntityType() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BaseFileSearchCondition)) {
            return false;
        }
        BaseFileSearchCondition other = (BaseFileSearchCondition) ((Object) o);
        if (!other.canEqual((Object) this)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Long this$minFileSize = this.getMinFileSize();
        Long other$minFileSize = other.getMinFileSize();
        if (this$minFileSize == null ? other$minFileSize != null : !((Object) this$minFileSize).equals(other$minFileSize)) {
            return false;
        }
        Long this$maxFileSize = this.getMaxFileSize();
        Long other$maxFileSize = other.getMaxFileSize();
        if (this$maxFileSize == null ? other$maxFileSize != null : !((Object) this$maxFileSize).equals(other$maxFileSize)) {
            return false;
        }
        Boolean this$isAttached = this.getIsAttached();
        Boolean other$isAttached = other.getIsAttached();
        if (this$isAttached == null ? other$isAttached != null : !((Object) this$isAttached).equals(other$isAttached)) {
            return false;
        }
        String this$mimeType = this.getMimeType();
        String other$mimeType = other.getMimeType();
        if (this$mimeType == null ? other$mimeType != null : !this$mimeType.equals(other$mimeType)) {
            return false;
        }
        String this$fileExtension = this.getFileExtension();
        String other$fileExtension = other.getFileExtension();
        if (this$fileExtension == null ? other$fileExtension != null : !this$fileExtension.equals(other$fileExtension)) {
            return false;
        }
        String this$entityType = this.getEntityType();
        String other$entityType = other.getEntityType();
        return !(this$entityType == null ? other$entityType != null : !this$entityType.equals(other$entityType));
    }

    protected boolean canEqual(Object other) {
        return other instanceof BaseFileSearchCondition;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        Long $minFileSize = this.getMinFileSize();
        result = result * 59 + ($minFileSize == null ? 43 : ((Object) $minFileSize).hashCode());
        Long $maxFileSize = this.getMaxFileSize();
        result = result * 59 + ($maxFileSize == null ? 43 : ((Object) $maxFileSize).hashCode());
        Boolean $isAttached = this.getIsAttached();
        result = result * 59 + ($isAttached == null ? 43 : ((Object) $isAttached).hashCode());
        String $mimeType = this.getMimeType();
        result = result * 59 + ($mimeType == null ? 43 : $mimeType.hashCode());
        String $fileExtension = this.getFileExtension();
        result = result * 59 + ($fileExtension == null ? 43 : $fileExtension.hashCode());
        String $entityType = this.getEntityType();
        result = result * 59 + ($entityType == null ? 43 : $entityType.hashCode());
        return result;
    }

    public static abstract class BaseFileSearchConditionBuilder<C extends BaseFileSearchCondition, B extends BaseFileSearchConditionBuilder<C, B>>
            extends BaseSearchCondition.BaseSearchConditionBuilder<C, B> {
        private String mimeType;
        private String fileExtension;
        private Long minFileSize;
        private Long maxFileSize;
        private Boolean isAttached;
        private String entityType;

        public B mimeType(String mimeType) {
            this.mimeType = mimeType;
            return (B) this.self();
        }

        public B fileExtension(String fileExtension) {
            this.fileExtension = fileExtension;
            return (B) this.self();
        }

        public B minFileSize(Long minFileSize) {
            this.minFileSize = minFileSize;
            return (B) this.self();
        }

        public B maxFileSize(Long maxFileSize) {
            this.maxFileSize = maxFileSize;
            return (B) this.self();
        }

        public B isAttached(Boolean isAttached) {
            this.isAttached = isAttached;
            return (B) this.self();
        }

        public B entityType(String entityType) {
            this.entityType = entityType;
            return (B) this.self();
        }

        protected abstract B self();

        public abstract C build();

        public String toString() {
            return "BaseFileSearchCondition.BaseFileSearchConditionBuilder(super=" + super.toString() + ", mimeType=" + this.mimeType + ", fileExtension=" + this.fileExtension + ", minFileSize=" + this.minFileSize + ", maxFileSize=" + this.maxFileSize + ", isAttached=" + this.isAttached + ", entityType=" + this.entityType + ")";
        }
    }

    private static final class BaseFileSearchConditionBuilderImpl
            extends BaseFileSearchConditionBuilder<BaseFileSearchCondition, BaseFileSearchConditionBuilderImpl> {
        private BaseFileSearchConditionBuilderImpl() {
        }

        @Override
        protected BaseFileSearchConditionBuilderImpl self() {
            return this;
        }

        @Override
        public BaseFileSearchCondition build() {
            return new BaseFileSearchCondition(this);
        }
    }
}


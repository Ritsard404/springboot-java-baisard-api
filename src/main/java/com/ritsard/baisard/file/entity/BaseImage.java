package com.ritsard.baisard.file.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="base_image")
public class BaseImage
extends BaseFile {
    @Column(name="original_width")
    private Integer originalWidth;
    @Column(name="original_height")
    private Integer originalHeight;
    @Column(name="resized_filepath")
    private String resizedFilepath;
    @Column(name="resized_url")
    private String resizedUrl;
    @Column(name="resized_width")
    private Integer resizedWidth;
    @Column(name="resized_height")
    private Integer resizedHeight;
    @Column(name="resized_size")
    private Long resizedSize;
    @Column(name="quality")
    private Double quality;
    @Column(name="has_thumbnail")
    private Boolean hasThumbnail;

    protected BaseImage(BaseImageBuilder<?, ?> b) {
        super(b);
        this.originalWidth = b.originalWidth;
        this.originalHeight = b.originalHeight;
        this.resizedFilepath = b.resizedFilepath;
        this.resizedUrl = b.resizedUrl;
        this.resizedWidth = b.resizedWidth;
        this.resizedHeight = b.resizedHeight;
        this.resizedSize = b.resizedSize;
        this.quality = b.quality;
        this.hasThumbnail = b.hasThumbnail;
    }

    public static BaseImageBuilder<?, ?> builder() {
        return new BaseImageBuilderImpl();
    }

    public BaseImageBuilder<?, ?> toBuilder() {
        return new BaseImageBuilderImpl().$fillValuesFrom(this);
    }

    public Integer getOriginalWidth() {
        return this.originalWidth;
    }

    public Integer getOriginalHeight() {
        return this.originalHeight;
    }

    public String getResizedFilepath() {
        return this.resizedFilepath;
    }

    public String getResizedUrl() {
        return this.resizedUrl;
    }

    public Integer getResizedWidth() {
        return this.resizedWidth;
    }

    public Integer getResizedHeight() {
        return this.resizedHeight;
    }

    public Long getResizedSize() {
        return this.resizedSize;
    }

    public Double getQuality() {
        return this.quality;
    }

    public Boolean getHasThumbnail() {
        return this.hasThumbnail;
    }

    public void setOriginalWidth(Integer originalWidth) {
        this.originalWidth = originalWidth;
    }

    public void setOriginalHeight(Integer originalHeight) {
        this.originalHeight = originalHeight;
    }

    public void setResizedFilepath(String resizedFilepath) {
        this.resizedFilepath = resizedFilepath;
    }

    public void setResizedUrl(String resizedUrl) {
        this.resizedUrl = resizedUrl;
    }

    public void setResizedWidth(Integer resizedWidth) {
        this.resizedWidth = resizedWidth;
    }

    public void setResizedHeight(Integer resizedHeight) {
        this.resizedHeight = resizedHeight;
    }

    public void setResizedSize(Long resizedSize) {
        this.resizedSize = resizedSize;
    }

    public void setQuality(Double quality) {
        this.quality = quality;
    }

    public void setHasThumbnail(Boolean hasThumbnail) {
        this.hasThumbnail = hasThumbnail;
    }

    public BaseImage() {
    }

    public BaseImage(Integer originalWidth, Integer originalHeight, String resizedFilepath, String resizedUrl, Integer resizedWidth, Integer resizedHeight, Long resizedSize, Double quality, Boolean hasThumbnail) {
        this.originalWidth = originalWidth;
        this.originalHeight = originalHeight;
        this.resizedFilepath = resizedFilepath;
        this.resizedUrl = resizedUrl;
        this.resizedWidth = resizedWidth;
        this.resizedHeight = resizedHeight;
        this.resizedSize = resizedSize;
        this.quality = quality;
        this.hasThumbnail = hasThumbnail;
    }

    public static abstract class BaseImageBuilder<C extends BaseImage, B extends BaseImageBuilder<C, B>>
    extends BaseFile.BaseFileBuilder<C, B> {
        private Integer originalWidth;
        private Integer originalHeight;
        private String resizedFilepath;
        private String resizedUrl;
        private Integer resizedWidth;
        private Integer resizedHeight;
        private Long resizedSize;
        private Double quality;
        private Boolean hasThumbnail;

        @Override
        protected B $fillValuesFrom(C instance) {
            super.$fillValuesFrom(instance);
            BaseImageBuilder.$fillValuesFromInstanceIntoBuilder(instance, this);
            return (B)((Object)this.self());
        }

        private static void $fillValuesFromInstanceIntoBuilder(BaseImage instance, BaseImageBuilder<?, ?> b) {
            b.originalWidth(instance.originalWidth);
            b.originalHeight(instance.originalHeight);
            b.resizedFilepath(instance.resizedFilepath);
            b.resizedUrl(instance.resizedUrl);
            b.resizedWidth(instance.resizedWidth);
            b.resizedHeight(instance.resizedHeight);
            b.resizedSize(instance.resizedSize);
            b.quality(instance.quality);
            b.hasThumbnail(instance.hasThumbnail);
        }

        public B originalWidth(Integer originalWidth) {
            this.originalWidth = originalWidth;
            return (B)((Object)this.self());
        }

        public B originalHeight(Integer originalHeight) {
            this.originalHeight = originalHeight;
            return (B)((Object)this.self());
        }

        public B resizedFilepath(String resizedFilepath) {
            this.resizedFilepath = resizedFilepath;
            return (B)((Object)this.self());
        }

        public B resizedUrl(String resizedUrl) {
            this.resizedUrl = resizedUrl;
            return (B)((Object)this.self());
        }

        public B resizedWidth(Integer resizedWidth) {
            this.resizedWidth = resizedWidth;
            return (B)((Object)this.self());
        }

        public B resizedHeight(Integer resizedHeight) {
            this.resizedHeight = resizedHeight;
            return (B)((Object)this.self());
        }

        public B resizedSize(Long resizedSize) {
            this.resizedSize = resizedSize;
            return (B)((Object)this.self());
        }

        public B quality(Double quality) {
            this.quality = quality;
            return (B)((Object)this.self());
        }

        public B hasThumbnail(Boolean hasThumbnail) {
            this.hasThumbnail = hasThumbnail;
            return (B)((Object)this.self());
        }

        @Override
        protected abstract B self();

        @Override
        public abstract C build();

        @Override
        public String toString() {
            return "BaseImage.BaseImageBuilder(super=" + super.toString() + ", originalWidth=" + this.originalWidth + ", originalHeight=" + this.originalHeight + ", resizedFilepath=" + this.resizedFilepath + ", resizedUrl=" + this.resizedUrl + ", resizedWidth=" + this.resizedWidth + ", resizedHeight=" + this.resizedHeight + ", resizedSize=" + this.resizedSize + ", quality=" + this.quality + ", hasThumbnail=" + this.hasThumbnail + ")";
        }
    }

    private static final class BaseImageBuilderImpl
    extends BaseImageBuilder<BaseImage, BaseImageBuilderImpl> {
        private BaseImageBuilderImpl() {
        }

        @Override
        protected BaseImageBuilderImpl self() {
            return this;
        }

        @Override
        public BaseImage build() {
            return new BaseImage(this);
        }
    }
}


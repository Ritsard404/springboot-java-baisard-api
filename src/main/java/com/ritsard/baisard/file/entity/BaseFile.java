/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.lodong.basemodule.entity.BaseEntity
 *  com.lodong.basemodule.entity.BaseEntity$BaseEntityBuilder
 *  com.lodong.utilsmodule.utils.UUIDManager
 *  jakarta.persistence.Column
 *  jakarta.persistence.Entity
 *  jakarta.persistence.Id
 *  jakarta.persistence.Index
 *  jakarta.persistence.Inheritance
 *  jakarta.persistence.InheritanceType
 *  jakarta.persistence.PrePersist
 *  jakarta.persistence.Table
 */
package com.ritsard.baisard.file.entity;

import com.ritsard.baisard.base.entity.BaseEntity;
import com.ritsard.baisard.utils.helper.UUIDManager;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "base_file", indexes = {@Index(name = "idx_base_file_uuid", columnList = "uuid_base_file"), @Index(name = "idx_base_file_is_deleted", columnList = "is_deleted"), @Index(name = "idx_base_file_is_attached", columnList = "is_attached"), @Index(name = "idx_base_file_created_at", columnList = "created_at")})
@Inheritance(strategy = InheritanceType.JOINED)
public class BaseFile
        extends BaseEntity {
    @Id
    @Column(name = "uuid_base_file", nullable = false, columnDefinition = "BINARY(16)")
    private UUID uuidBaseFile;
    @Column(name = "filename", length = 255)
    private String filename;
    @Column(name = "original_filename", length = 255)
    private String originalFilename;
    @Column(name = "filepath", length = 1000)
    private String filepath;
    @Column(name = "url", length = 1000)
    private String url;
    @Column(name = "mime_type", length = 100)
    private String mimeType;
    @Column(name = "file_size")
    private Long fileSize;
    @Column(name = "file_extension", length = 20)
    private String fileExtension;
    @Column(name = "is_attached", nullable = false)
    private Boolean isAttached;
    @Column(name = "entity_type", length = 50)
    private String entityType;
    @Column(name = "entity_id")
    private UUID entityId;
    @Column(name = "attached_at")
    private Instant attachedAt;

    @PrePersist
    protected void onCreate() {
        if (this.uuidBaseFile == null) {
            this.uuidBaseFile = UUIDManager.generateUUIDv7();
        }
    }

    public void attachToEntity(String entityType, UUID entityId) {
        this.isAttached = true;
        this.entityType = entityType;
        this.entityId = entityId;
        this.attachedAt = Instant.now();
    }

    public void detachFromEntity() {
        this.isAttached = false;
        this.entityType = null;
        this.entityId = null;
        this.attachedAt = null;
    }

    private static Boolean $default$isAttached() {
        return false;
    }

    protected BaseFile(BaseFileBuilder<?, ?> b) {
        super(b);
        this.uuidBaseFile = b.uuidBaseFile;
        this.filename = b.filename;
        this.originalFilename = b.originalFilename;
        this.filepath = b.filepath;
        this.url = b.url;
        this.mimeType = b.mimeType;
        this.fileSize = b.fileSize;
        this.fileExtension = b.fileExtension;
        this.isAttached = b.isAttached$set ? b.isAttached$value : BaseFile.$default$isAttached();
        this.entityType = b.entityType;
        this.entityId = b.entityId;
        this.attachedAt = b.attachedAt;
    }

    public static BaseFileBuilder<?, ?> builder() {
        return new BaseFileBuilderImpl();
    }

    public BaseFileBuilder<?, ?> toBuilder() {
        return new BaseFileBuilderImpl().$fillValuesFrom(this);
    }

    public UUID getUuidBaseFile() {
        return this.uuidBaseFile;
    }

    public String getFilename() {
        return this.filename;
    }

    public String getOriginalFilename() {
        return this.originalFilename;
    }

    public String getFilepath() {
        return this.filepath;
    }

    public String getUrl() {
        return this.url;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public Long getFileSize() {
        return this.fileSize;
    }

    public String getFileExtension() {
        return this.fileExtension;
    }

    public Boolean getIsAttached() {
        return this.isAttached;
    }

    public String getEntityType() {
        return this.entityType;
    }

    public UUID getEntityId() {
        return this.entityId;
    }

    public Instant getAttachedAt() {
        return this.attachedAt;
    }

    public void setUuidBaseFile(UUID uuidBaseFile) {
        this.uuidBaseFile = uuidBaseFile;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public void setIsAttached(Boolean isAttached) {
        this.isAttached = isAttached;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public void setAttachedAt(Instant attachedAt) {
        this.attachedAt = attachedAt;
    }

    public BaseFile() {
        this.isAttached = BaseFile.$default$isAttached();
    }

    public BaseFile(UUID uuidBaseFile, String filename, String originalFilename, String filepath, String url, String mimeType, Long fileSize, String fileExtension, Boolean isAttached, String entityType, UUID entityId, Instant attachedAt) {
        this.uuidBaseFile = uuidBaseFile;
        this.filename = filename;
        this.originalFilename = originalFilename;
        this.filepath = filepath;
        this.url = url;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.fileExtension = fileExtension;
        this.isAttached = isAttached;
        this.entityType = entityType;
        this.entityId = entityId;
        this.attachedAt = attachedAt;
    }

    public static abstract class BaseFileBuilder<C extends BaseFile, B extends BaseFileBuilder<C, B>>
            extends BaseEntity.BaseEntityBuilder<C, B> {
        private UUID uuidBaseFile;
        private String filename;
        private String originalFilename;
        private String filepath;
        private String url;
        private String mimeType;
        private Long fileSize;
        private String fileExtension;
        private boolean isAttached$set;
        private Boolean isAttached$value;
        private String entityType;
        private UUID entityId;
        private Instant attachedAt;

        protected B $fillValuesFrom(C instance) {
            super.$fillValuesFrom(instance);
            BaseFileBuilder.$fillValuesFromInstanceIntoBuilder(instance, this);
            return (B) this.self();
        }

        private static void $fillValuesFromInstanceIntoBuilder(BaseFile instance, BaseFileBuilder<?, ?> b) {
            b.uuidBaseFile(instance.uuidBaseFile);
            b.filename(instance.filename);
            b.originalFilename(instance.originalFilename);
            b.filepath(instance.filepath);
            b.url(instance.url);
            b.mimeType(instance.mimeType);
            b.fileSize(instance.fileSize);
            b.fileExtension(instance.fileExtension);
            b.isAttached(instance.isAttached);
            b.entityType(instance.entityType);
            b.entityId(instance.entityId);
            b.attachedAt(instance.attachedAt);
        }

        public B uuidBaseFile(UUID uuidBaseFile) {
            this.uuidBaseFile = uuidBaseFile;
            return (B) this.self();
        }

        public B filename(String filename) {
            this.filename = filename;
            return (B) this.self();
        }

        public B originalFilename(String originalFilename) {
            this.originalFilename = originalFilename;
            return (B) this.self();
        }

        public B filepath(String filepath) {
            this.filepath = filepath;
            return (B) this.self();
        }

        public B url(String url) {
            this.url = url;
            return (B) this.self();
        }

        public B mimeType(String mimeType) {
            this.mimeType = mimeType;
            return (B) this.self();
        }

        public B fileSize(Long fileSize) {
            this.fileSize = fileSize;
            return (B) this.self();
        }

        public B fileExtension(String fileExtension) {
            this.fileExtension = fileExtension;
            return (B) this.self();
        }

        public B isAttached(Boolean isAttached) {
            this.isAttached$value = isAttached;
            this.isAttached$set = true;
            return (B) this.self();
        }

        public B entityType(String entityType) {
            this.entityType = entityType;
            return (B) this.self();
        }

        public B entityId(UUID entityId) {
            this.entityId = entityId;
            return (B) this.self();
        }

        public B attachedAt(Instant attachedAt) {
            this.attachedAt = attachedAt;
            return (B) this.self();
        }

        protected abstract B self();

        public abstract C build();

        public String toString() {
            return "BaseFile.BaseFileBuilder(super=" + super.toString() + ", uuidBaseFile=" + String.valueOf(this.uuidBaseFile) + ", filename=" + this.filename + ", originalFilename=" + this.originalFilename + ", filepath=" + this.filepath + ", url=" + this.url + ", mimeType=" + this.mimeType + ", fileSize=" + this.fileSize + ", fileExtension=" + this.fileExtension + ", isAttached$value=" + this.isAttached$value + ", entityType=" + this.entityType + ", entityId=" + String.valueOf(this.entityId) + ", attachedAt=" + String.valueOf(this.attachedAt) + ")";
        }
    }

    private static final class BaseFileBuilderImpl
            extends BaseFileBuilder<BaseFile, BaseFileBuilderImpl> {
        private BaseFileBuilderImpl() {
        }

        @Override
        protected BaseFileBuilderImpl self() {
            return this;
        }

        @Override
        public BaseFile build() {
            return new BaseFile(this);
        }
    }
}


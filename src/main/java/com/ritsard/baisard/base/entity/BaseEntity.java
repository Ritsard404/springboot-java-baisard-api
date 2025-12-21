package com.ritsard.baisard.base.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Generated;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@MappedSuperclass
public abstract class BaseEntity {
    @Column(
            name = "created_at",
            updatable = false
    )
    @CreationTimestamp
    protected Instant createdAt;
    @Column(
            name = "updated_at"
    )
    @UpdateTimestamp
    protected Instant updatedAt;
    @Column(
            name = "is_deleted",
            nullable = false
    )
    protected boolean isDeleted;
    @Column(
            name = "deleted_at"
    )
    protected Instant deletedAt;

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = Instant.now();
    }

    public void restore() {
        this.isDeleted = false;
        this.deletedAt = null;
    }

    public boolean isDeleted() {
        return this.isDeleted;
    }

    public boolean isActive() {
        return !this.isDeleted;
    }

    @Generated
    private static boolean $default$isDeleted() {
        return false;
    }

    @Generated
    protected BaseEntity(final BaseEntity.BaseEntityBuilder<?, ?> b) {
        this.createdAt = b.createdAt;
        this.updatedAt = b.updatedAt;
        if (b.isDeleted$set) {
            this.isDeleted = b.isDeleted$value;
        } else {
            this.isDeleted = $default$isDeleted();
        }

        this.deletedAt = b.deletedAt;
    }

    @Generated
    public Instant getCreatedAt() {
        return this.createdAt;
    }

    @Generated
    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    @Generated
    public Instant getDeletedAt() {
        return this.deletedAt;
    }

    @Generated
    public void setCreatedAt(final Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Generated
    public void setUpdatedAt(final Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Generated
    public void setDeleted(final boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Generated
    public void setDeletedAt(final Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Generated
    protected BaseEntity() {
        this.isDeleted = $default$isDeleted();
    }

    @Generated
    public BaseEntity(final Instant createdAt, final Instant updatedAt, final boolean isDeleted, final Instant deletedAt) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
    }

    @Generated
    public abstract static class BaseEntityBuilder<C extends BaseEntity, B extends BaseEntity.BaseEntityBuilder<C, B>> {
        @Generated
        private Instant createdAt;
        @Generated
        private Instant updatedAt;
        @Generated
        private boolean isDeleted$set;
        @Generated
        private boolean isDeleted$value;
        @Generated
        private Instant deletedAt;

        @Generated
        protected B $fillValuesFrom(final C instance) {
            $fillValuesFromInstanceIntoBuilder(instance, this);
            return (B)this.self();
        }

        @Generated
        private static void $fillValuesFromInstanceIntoBuilder(final BaseEntity instance, final BaseEntity.BaseEntityBuilder<?, ?> b) {
            b.createdAt(instance.createdAt);
            b.updatedAt(instance.updatedAt);
            b.isDeleted(instance.isDeleted);
            b.deletedAt(instance.deletedAt);
        }

        @Generated
        public B createdAt(final Instant createdAt) {
            this.createdAt = createdAt;
            return (B)this.self();
        }

        @Generated
        public B updatedAt(final Instant updatedAt) {
            this.updatedAt = updatedAt;
            return (B)this.self();
        }

        @Generated
        public B isDeleted(final boolean isDeleted) {
            this.isDeleted$value = isDeleted;
            this.isDeleted$set = true;
            return (B)this.self();
        }

        @Generated
        public B deletedAt(final Instant deletedAt) {
            this.deletedAt = deletedAt;
            return (B)this.self();
        }

        @Generated
        protected abstract B self();

        @Generated
        public abstract C build();

        @Generated
        public String toString() {
            return "BaseEntity.BaseEntityBuilder(createdAt=" + this.createdAt + ", updatedAt=" + this.updatedAt + ", isDeleted$value=" + this.isDeleted$value + ", deletedAt=" + this.deletedAt + ")";
        }
    }
}


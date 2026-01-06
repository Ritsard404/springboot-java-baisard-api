package com.ritsard.baisard.domain.inventory.entity;

import com.ritsard.baisard.base.entity.BaseEntity;
import com.ritsard.baisard.utils.helper.UUIDManager;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "category")
public class Category extends BaseEntity {
    @Id
    @Column(name = "uuid_category", nullable = false)
    @Builder.Default
    private UUID uuidCategory = UUIDManager.generateUUIDv7();

    @Column(name = "category_name", length = 255)
    private String categoryName;
}

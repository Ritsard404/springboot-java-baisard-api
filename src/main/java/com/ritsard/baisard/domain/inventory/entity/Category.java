package com.ritsard.baisard.domain.inventory.entity;

import com.ritsard.baisard.base.entity.BaseEntity;
import com.ritsard.baisard.domain.member.entity.Company;
import com.ritsard.baisard.utils.helper.UUIDManager;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE category SET is_deleted = true, deleted_at = now() WHERE uuid_category = ?")
@SQLRestriction("is_deleted = false")
public class Category extends BaseEntity {
    @Id
    @Column(name = "uuid_category", nullable = false)
    @Builder.Default
    private UUID uuidCategory = UUIDManager.generateUUIDv7();

    @Column(name = "category_name")
    private String categoryName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uuid_company")
    private Company company;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Product> products = new ArrayList<>();
}

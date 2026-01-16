package com.ritsard.baisard.domain.inventory.entity;

import com.ritsard.baisard.base.entity.BaseEntity;
import com.ritsard.baisard.domain.inventory.enums.ItemType;
import com.ritsard.baisard.domain.inventory.enums.VatType;
import com.ritsard.baisard.file.entity.v2.ImageFileInfo;
import com.ritsard.baisard.file.service.v4.FileLoadable;
import com.ritsard.baisard.utils.helper.UUIDManager;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE product SET is_deleted = true, deleted_at = now() WHERE uuid_product = ?")
@SQLRestriction("is_deleted = false")
@Table(
        name = "product",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_product_name_category",
                        columnNames = {"name", "uuid_category"}
                )
        }
)
public class Product extends BaseEntity implements FileLoadable<ImageFileInfo> {
    @Id
    @Column(name = "uuid_product", nullable = false)
    @Builder.Default
    private UUID uuidProduct = UUIDManager.generateUUIDv7();

//    @Column(name = "prod_id", nullable = false, unique = true, length = 50)
//    @Builder.Default
//    private String prodId = "";

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "product_image_url", length = 500)
    private String productImageUrl;

    @Column(name = "barcode", length = 100)
//    @Builder.Default
    private String barcode;

    @Column(name = "base_unit", nullable = false, length = 50)
    @Builder.Default
    private String baseUnit = "";

    @Column(name = "quantity", precision = 19, scale = 4)
    private BigDecimal quantity;

    @Column(name = "cost", nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal cost = BigDecimal.ZERO;

    @Column(name = "price", nullable = false, precision = 19, scale = 4)
    private BigDecimal price;

    @Column(name = "is_available", nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;

    @Column(name = "item_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ItemType itemType = ItemType.RESALE;

    @Column(name = "vat_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VatType vatType = VatType.VATABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uuid_category", nullable = false)
    private Category category;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "uuid_product"))
    @Builder.Default
    private List<ImageFileInfo> images = new ArrayList<>();

    @Override
    public List<ImageFileInfo> getFileList() {
        return this.images;
    }

    @Override
    public void setFileList(List<ImageFileInfo> files) {
        this.images = files != null ? files : new ArrayList<>();
    }

    @Override
    public UUID getId() {
        return this.uuidProduct;
    }
}

package com.ritsard.baisard.domain.member.entity;

import com.ritsard.baisard.base.entity.BaseEntity;
import com.ritsard.baisard.file.entity.v2.ImageFileInfo;
import com.ritsard.baisard.file.service.v4.FileLoadable;
import com.ritsard.baisard.jwt.model.entity.BaseMember;
import com.ritsard.baisard.utils.helper.UUIDManager;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "company")
public class Company extends BaseEntity implements FileLoadable<ImageFileInfo> {
    @Id
    @Column(name = "uuid_company", nullable = false)
    @Builder.Default
    private UUID uuidCompany = UUIDManager.generateUUIDv7();

    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "code")
    private String code;
    @Column(name = "email")
    private String email;
    @Column(name = "phone")
    private String phone;

    @Column(name = "logo_img_url", length = 500)
    private String logoImageUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean approved = false;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "company")
    @Builder.Default
    private List<Member> Members = new ArrayList<>();


    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "company_images", joinColumns = @JoinColumn(name = "uuid_company"))
    @Builder.Default
    private List<ImageFileInfo> images = new ArrayList<>();


    @Override
    public List<ImageFileInfo> getFileList() {
        return this.images;
    }

    @Override
    public void setFileList(List<ImageFileInfo> files) {
        this.images = files;
    }

    @Override
    public UUID getId() {
        return this.getUuidCompany();
    }

}

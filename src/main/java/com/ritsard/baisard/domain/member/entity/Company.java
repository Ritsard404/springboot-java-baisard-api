package com.ritsard.baisard.domain.member.entity;

import com.ritsard.baisard.base.entity.BaseEntity;
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
public class Company extends BaseEntity {
    @Id
    @Column(name = "uuid_company", nullable = false)
    @Builder.Default
    private UUID uuidCompany = UUIDManager.generateUUIDv7();

    @Column(name = "name")
    private String name;
    @Column(name = "code")
    private String code;
    @Column(name = "email")
    private String email;
    @Column(name = "phone")
    private String phone;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "company")
    @Builder.Default
    private List<BaseMember> baseMembers = new ArrayList<>();

}

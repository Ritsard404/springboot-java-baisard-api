package com.ritsard.baisard.domain.member.entity;


import com.ritsard.baisard.domain.member.enums.MemberApprovalStatus;
import com.ritsard.baisard.domain.member.enums.PermissionType;
import com.ritsard.baisard.file.entity.v2.ImageFileInfo;
import com.ritsard.baisard.file.service.v4.FileLoadable;
import com.ritsard.baisard.global.enums.AccessPathType;
import com.ritsard.baisard.jwt.model.entity.BaseMember;
import com.ritsard.baisard.jwt.model.entity.LoginCredential;
import com.ritsard.baisard.jwt.model.entity.Permission;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "member")
@Setter
public class Member extends BaseMember implements FileLoadable<ImageFileInfo> {

    @Column(name = "referral_source", length = 500)
    private String referralSource;

    @Column(name = "access_path")
    private AccessPathType accessPath;

    @Column(name = "sms_code", length = 6)
    private String smsCode;

    @Column(name = "smsExpiration")
    private Instant smsExpiration;

    @Column(name = "introduction", columnDefinition = "TEXT")
    private String introduction;

    @Column(name = "profile_img_url", length = 500)
    private String profileImageUrl;

    @Column(name = "logo_img_url", length = 500)
    private String logoImageUrl;

    @Column(name = "logo_url_link", length = 500)
    private String logoUrlLink;

    @Column(name = "member_is_deleted", nullable = false)
    @Builder.Default
    private Boolean memberIsDeleted = false;

    @Column(name = "member_deleted_at")
    private Instant memberDeletedAt;

    // --- Approval ---
    @Enumerated(EnumType.STRING)
//    @Column(name = "approval_status", nullable = false)
    @Column(name = "approval_status")
    @Builder.Default
    private MemberApprovalStatus approvalStatus = MemberApprovalStatus.PENDING;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "approved_by")
    private UUID approvedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uuid_company")
    private Company company;


    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "member_images", joinColumns = @JoinColumn(name = "uuid_member"))
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
        return this.getUuidMember();
    }

    public String getClassification() {
        List<String> types = this.getPermissions().stream()
                .map(Permission::getPermissionType)
                .map(String::toUpperCase)
                .toList();

        if (types.contains("SUPERADMIN")) return PermissionType.SUPERADMIN.getDescription();
        if (types.contains("ADMIN")) return PermissionType.ADMIN.getDescription();
        if (types.contains("CASHIER")) return PermissionType.CASHIER.getDescription();
        return PermissionType.USER.getDescription();
    }

    public String getIdentifier() {
        return getLoginCredentials().stream()
                .findFirst()
                .map(LoginCredential::getIdentifier)
                .orElse(null);
    }

    // Add soft delete methods for Member
    public void softDeleteMember() {
        this.memberIsDeleted = true;
        this.memberDeletedAt = Instant.now();
    }

    public void restoreMember() {
        this.memberIsDeleted = false;
        this.memberDeletedAt = null;
    }

    public boolean isMemberDeleted() {
        return this.memberIsDeleted;
    }

    public boolean isMemberActive() {
        return !this.memberIsDeleted;
    }

    // --- Guards ---
    public boolean isApproved() {
        return approvalStatus == MemberApprovalStatus.APPROVED;
    }

    public void approve(UUID superAdminId) {
        this.approvalStatus = MemberApprovalStatus.APPROVED;
        this.approvedAt = Instant.now();
        this.approvedBy = superAdminId;
    }
}
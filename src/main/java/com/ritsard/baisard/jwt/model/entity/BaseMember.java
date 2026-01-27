package com.ritsard.baisard.jwt.model.entity;

import com.ritsard.baisard.base.entity.BaseEntity;
import com.ritsard.baisard.utils.helper.EncryptedFieldConverter;
import com.ritsard.baisard.utils.helper.UUIDManager;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "base_member",
        indexes = {
                @Index(name = "idx_base_member_is_deleted", columnList = "is_deleted"),
                @Index(name = "idx_base_member_created_at", columnList = "created_at"),
                @Index(name = "idx_base_member_email", columnList = "email"),
                @Index(name = "idx_base_member_phone", columnList = "phone_number"),
                @Index(name = "idx_base_member_nickname", columnList = "nickname")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_base_member_nickname_is_deleted", columnNames = {"nickname", "is_deleted"}),
                @UniqueConstraint(name = "uk_base_member_phone_is_deleted", columnNames = {"phone_number", "is_deleted"})
        }
)
@SQLDelete(sql = "UPDATE base_member SET is_deleted = true, deleted_at = NOW() WHERE uuid_member = ?")
//@SQLRestriction("is_deleted = false")
@Inheritance(
        strategy = InheritanceType.JOINED
)
public class BaseMember extends BaseEntity {

    @Id
    @Builder.Default
    @Column(name = "uuid_member", nullable = false)
    private UUID uuidMember = UUIDManager.generateUUIDv7();

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "email", length = 100)
    private String email;

//    @Convert(converter = EncryptedFieldConverter.class)
    @Column(name = "phone_number", length = 256)
    private String phoneNumber;

    @Column(name = "birthdate")
    private LocalDate birthdate;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LoginCredential> loginCredentials = new ArrayList<>();


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "member_permission",
            joinColumns = @JoinColumn(name = "member_uuid"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    /* =====================
       Domain Behaviors
       ===================== */

    public void deleteMember() {
        this.softDelete();
    }

    public void restoreMember() {
        this.restore();
    }
}

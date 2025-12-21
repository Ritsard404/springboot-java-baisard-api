package com.ritsard.baisard.jwt.repository.login;

import com.ritsard.baisard.jwt.model.entity.LoginCredential;
import com.ritsard.baisard.jwt.model.enums.LoginType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface LoginCredentialRepository extends JpaRepository<LoginCredential, UUID>, LoginCredentialRepositoryCustom {
    Optional<LoginCredential> findByIdentifierAndLoginType(@NotBlank String identifier, @NotNull LoginType loginType);

    boolean existsByIdentifier(String identifier);

    @Query("    SELECT lc.identifier\n    FROM LoginCredential lc\n    WHERE lc.member.name = :name AND lc.member.email = :email\n")
    Optional<String> findIdentifierByNameAndEmail(String name, String email);

    @Query("SELECT lc FROM LoginCredential lc WHERE lc.identifier = :identifier AND lc.member.email = :email")
    Optional<LoginCredential> findByIdentifierAndEmail(String identifier, String email);

    Optional<LoginCredential> findByIdentifier(String identifier);
}
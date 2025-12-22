package com.ritsard.baisard.jwt.repository.login;

import com.ritsard.baisard.jwt.model.entity.LoginCredential;
import com.ritsard.baisard.jwt.model.enums.LoginType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface LoginCredentialRepositoryCustom {
    Optional<LoginCredential> findWithMember(UUID uuidLoginCredential);

    Optional<LoginCredential> findWithMemberByIdentifier(String identifier);
}


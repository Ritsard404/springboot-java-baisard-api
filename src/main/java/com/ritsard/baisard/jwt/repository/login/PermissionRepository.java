package com.ritsard.baisard.jwt.repository.login;

import com.ritsard.baisard.jwt.model.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByPermissionType(String permissionType);
}

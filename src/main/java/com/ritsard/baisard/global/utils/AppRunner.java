package com.ritsard.baisard.global.utils;

import com.ritsard.baisard.domain.enums.PermissionType;
import com.ritsard.baisard.jwt.model.entity.Permission;
import com.ritsard.baisard.jwt.repository.login.PermissionRepository;
import com.ritsard.baisard.utils.config.ConfigurationRunnerBase;
import com.ritsard.baisard.utils.helper.AESConverter;
import org.springframework.boot.ApplicationArguments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class AppRunner extends ConfigurationRunnerBase {
    private final PermissionRepository permissionRepository;

    public AppRunner(AESConverter aesConverter, PermissionRepository permissionRepository) {
        super(aesConverter);
        this.permissionRepository = permissionRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initEnumValuesToDatabase(PermissionType.class, Permission.class, permissionRepository);
        super.run(args);
    }

    @Override
    public <E extends Enum<E>, T> void initEnumValuesToDatabase(Class<E> enumClass, Class<T> entityClass, JpaRepository<T, ?> repository) {
        super.initEnumValuesToDatabase(enumClass, entityClass, repository);
    }

    public <E extends Enum<E>, T> void initAllEnumValuesToDatabase(
            Class<E> enumClass,
            Function<E, T> entityMapper,
            Function<T, String> keyExtractor,
            JpaRepository<T, ?> repository
    ) {
        for (E enumVal : enumClass.getEnumConstants()) {
            String key = keyExtractor.apply(entityMapper.apply(enumVal));
            boolean exists = repository.findAll().stream()
                    .anyMatch(entity -> key.equals(keyExtractor.apply(entity)));

            if (!exists) {
                repository.save(entityMapper.apply(enumVal));
                System.out.println("✅ Saved: " + key);
            } else {
                System.out.println("⭕ Already exists: " + key);
            }
        }
    }
}

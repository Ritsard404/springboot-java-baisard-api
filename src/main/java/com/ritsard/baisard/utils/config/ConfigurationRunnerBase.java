package com.ritsard.baisard.utils.config;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import com.ritsard.baisard.utils.helper.AESConverter;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationRunnerBase implements ApplicationRunner {
    private final AESConverter aesConverter;

    public void run(ApplicationArguments args) throws Exception {
        // No-op
    }

    public <E extends Enum<E>, T> void initEnumValuesToDatabase(
            Class<E> enumClass,
            Class<T> entityClass,
            JpaRepository<T, ?> repository
    ) {
        List<String> excludedFields = List.of("id", "createdAt", "updatedAt");

        Field targetField = Arrays.stream(entityClass.getDeclaredFields())
                .filter(f -> !excludedFields.contains(f.getName()))
                .filter(f -> f.getType() == String.class)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No suitable String field found."
                ));

        String fieldName = targetField.getName();

        for (Enum enumVal : enumClass.getEnumConstants()) {
            String value = enumVal.name();

            boolean exists = repository.findAll().stream().anyMatch(entity -> {
                try {
                    targetField.setAccessible(true);
                    Object fieldValue = targetField.get(entity);
                    return value.equals(fieldValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            });

            if (!exists) {
                try {
                    T instance = entityClass.getDeclaredConstructor().newInstance();
                    targetField.setAccessible(true);
                    targetField.set(instance, value);
                    repository.save(instance);
                    System.out.println("✅ Added: " + value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                continue;
            }

            System.out.println("⭕ Already exists: " + value);
        }
    }

    public ConfigurationRunnerBase(AESConverter aesConverter) {
        this.aesConverter = aesConverter;
    }
}

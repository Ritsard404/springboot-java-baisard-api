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
public class ConfigurationRunnerBase
implements ApplicationRunner {
    private final AESConverter aesConverter;

    public void run(ApplicationArguments args) throws Exception {
    }

    public <E extends Enum<E>, T> void initEnumValuesToDatabase(Class<E> enumClass, Class<T> entityClass, JpaRepository<T, ?> repository) {
        List<String> excludedFields = List.of("id", "createdAt", "updatedAt");
        Field targetField = Arrays.stream(entityClass.getDeclaredFields()).filter(f -> !excludedFields.contains(f.getName())).filter(f -> f.getType() == String.class).findFirst().orElseThrow(() -> new IllegalArgumentException("\uc801\uc808\ud55c String \ud0c0\uc785 \ud544\ub4dc\ub97c \ucc3e\uc744 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4."));
        String fieldName = targetField.getName();
        for (Enum enumVal : (Enum[])enumClass.getEnumConstants()) {
            String value = enumVal.name();
            boolean exists = repository.findAll().stream().anyMatch(entity -> {
                try {
                    targetField.setAccessible(true);
                    Object fieldValue = targetField.get(entity);
                    return value.equals(fieldValue);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            });
            if (!exists) {
                try {
                    T instance = entityClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                    targetField.setAccessible(true);
                    targetField.set(instance, value);
                    repository.save(instance);
                    System.out.println("\u2705 \uc800\uc7a5\ub428: " + value);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                continue;
            }
            System.out.println("\u2b55 \uc774\ubbf8 \uc788\uc74c: " + value);
        }
    }

    public ConfigurationRunnerBase(AESConverter aesConverter) {
        this.aesConverter = aesConverter;
    }
}


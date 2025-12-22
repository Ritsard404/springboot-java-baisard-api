package com.ritsard.baisard.jwt.utils.permission;

import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean({PermissionTypeProvider.class})
public class DynamicPermissionTypeProvider implements PermissionTypeProvider {

    private final PermissionType[] types = this.detectEnumBasedPermissionTypes();

    @Override
    public PermissionType[] getPermissionTypes() {
        return this.types;
    }

    private PermissionType[] detectEnumBasedPermissionTypes() {
        try {
            // Using Scanners.SubTypes is the modern way for Reflections 0.10.2+
            Reflections reflections = new Reflections("com.ritsard");

            Set<Class<? extends PermissionTypeProvider>> candidates = reflections
                    .getSubTypesOf(PermissionTypeProvider.class)
                    .stream()
                    .filter(Class::isEnum)
                    .collect(Collectors.toSet());

            if (candidates.isEmpty()) {
                throw new IllegalStateException("No enum found that implements PermissionTypeProvider.");
            }

            // Get the first matching Enum class
            Class<? extends PermissionTypeProvider> enumClass = candidates.iterator().next();

            // Get the enum constants (the actual instances of the enum)
            PermissionTypeProvider[] constants = enumClass.getEnumConstants();

            if (constants != null && constants.length > 0) {
                // Since it's an enum implementing the interface, we can just call the method on the first constant
                return constants[0].getPermissionTypes();
            }

            return new PermissionType[0];

        } catch (Exception e) {
            throw new RuntimeException("🔴 Failed to automatically detect PermissionTypeProvider enum", e);
        }
    }
}
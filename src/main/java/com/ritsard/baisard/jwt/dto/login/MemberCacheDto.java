package com.ritsard.baisard.jwt.dto.login;

import com.ritsard.baisard.jwt.model.entity.BaseMember;
import com.ritsard.baisard.jwt.model.entity.Permission;
import lombok.*;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class MemberCacheDto implements Serializable {
    private UUID uuid;
    private String name;
    private String email;
    private Set<String> permissions;
    private static final long serialVersionUID = 1L;

    public static MemberCacheDto from(BaseMember member) {
        return builder().uuid(member.getUuidMember()).name(member.getName()).email(member.getEmail()).permissions((Set) member.getPermissions().stream().map(Permission::getPermissionType).collect(Collectors.toSet())).build();
    }
}

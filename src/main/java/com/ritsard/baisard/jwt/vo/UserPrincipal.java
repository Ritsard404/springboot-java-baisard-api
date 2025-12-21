package com.ritsard.baisard.jwt.vo;

import lombok.Generated;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

public class UserPrincipal implements UserDetails {
    private final UUID memberUuid;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String username;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    public String getPassword() {
        return null;
    }

    public String getUsername() {
        return this.username;
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return true;
    }

    @Generated
    public UUID getMemberUuid() {
        return this.memberUuid;
    }

    @Generated
    public UserPrincipal(final UUID memberUuid, final Collection<? extends GrantedAuthority> authorities, final String username) {
        this.memberUuid = memberUuid;
        this.authorities = authorities;
        this.username = username;
    }
}

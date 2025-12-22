/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.core.userdetails.UsernameNotFoundException
 */
package com.ritsard.baisard.utils.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface CustomUserDetailService {
    public Object loadUserByUsername(String var1) throws UsernameNotFoundException;
}


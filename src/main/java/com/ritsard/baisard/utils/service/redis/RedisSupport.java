/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.redis.core.RedisTemplate
 */
package com.ritsard.baisard.utils.service.redis;

import org.springframework.data.redis.core.RedisTemplate;

public interface RedisSupport {
    public RedisTemplate<String, String> getRedisTemplate();
}


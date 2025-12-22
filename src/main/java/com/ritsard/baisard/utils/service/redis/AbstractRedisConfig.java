/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  org.springframework.cache.annotation.EnableCaching
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Primary
 *  org.springframework.data.redis.cache.RedisCacheConfiguration
 *  org.springframework.data.redis.cache.RedisCacheManager
 *  org.springframework.data.redis.connection.RedisConnectionFactory
 *  org.springframework.data.redis.connection.RedisPassword
 *  org.springframework.data.redis.connection.RedisStandaloneConfiguration
 *  org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
 *  org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
 *  org.springframework.data.redis.core.RedisTemplate
 *  org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
 *  org.springframework.data.redis.serializer.RedisSerializationContext$SerializationPair
 *  org.springframework.data.redis.serializer.RedisSerializer
 *  org.springframework.data.redis.serializer.StringRedisSerializer
 */
package com.ritsard.baisard.utils.service.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@EnableCaching
public abstract class AbstractRedisConfig {
    private final ObjectMapper objectMapper;

    protected abstract String getHost();

    protected int getPort() {
        return 6379;
    }

    protected int getDatabase() {
        return 0;
    }

    protected String getPassword() {
        return "";
    }

    protected boolean useSsl() {
        return false;
    }

    protected Duration getCommandTimeout() {
        return Duration.ofSeconds(3L);
    }

    protected Duration getDefaultTtl() {
        return Duration.ofMinutes(30L);
    }

    protected boolean cacheNullValues() {
        return false;
    }

    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(this.getHost());
        config.setPort(this.getPort());
        config.setDatabase(this.getDatabase());
        if (this.getPassword() != null && !this.getPassword().isEmpty()) {
            config.setPassword(RedisPassword.of((String)this.getPassword()));
        }
        System.out.println("Redis \uc5f0\uacb0 \uc124\uc815: " + this.getHost() + ":" + this.getPort() + ", DB: " + this.getDatabase());
        System.out.println("Redis \ube44\ubc00\ubc88\ud638 \uc124\uc815 \uc5ec\ubd80: " + (this.getPassword() != null && !this.getPassword().isEmpty()));
        System.out.println("Redis SSL \uc0ac\uc6a9 \uc5ec\ubd80: " + this.useSsl());
        if (this.useSsl()) {
            LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder().commandTimeout(this.getCommandTimeout()).useSsl().build();
            return new LettuceConnectionFactory(config, clientConfig);
        }
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder().commandTimeout(this.getCommandTimeout()).build();
        return new LettuceConnectionFactory(config, clientConfig);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate template = new RedisTemplate();
        template.setConnectionFactory(this.redisConnectionFactory());
        StringRedisSerializer serializer = new StringRedisSerializer();
        template.setKeySerializer((RedisSerializer)serializer);
        template.setValueSerializer((RedisSerializer)serializer);
        template.setHashKeySerializer((RedisSerializer)serializer);
        template.setHashValueSerializer((RedisSerializer)serializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    @Primary
    public RedisTemplate<String, Object> objectRedisTemplate() {
        RedisTemplate template = new RedisTemplate();
        template.setConnectionFactory(this.redisConnectionFactory());
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(this.objectMapper);
        template.setKeySerializer((RedisSerializer)new StringRedisSerializer());
        template.setValueSerializer((RedisSerializer)jsonSerializer);
        template.setHashKeySerializer((RedisSerializer)new StringRedisSerializer());
        template.setHashValueSerializer((RedisSerializer)jsonSerializer);
        template.setDefaultSerializer((RedisSerializer)jsonSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig().entryTtl(this.getDefaultTtl()).disableCachingNullValues();
        if (this.cacheNullValues()) {
            cacheConfig = cacheConfig.disableCachingNullValues();
        }
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(this.objectMapper);
        cacheConfig = cacheConfig.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer((RedisSerializer)jsonSerializer));
        return RedisCacheManager.builder((RedisConnectionFactory)connectionFactory).cacheDefaults(cacheConfig).build();
    }

    public AbstractRedisConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}


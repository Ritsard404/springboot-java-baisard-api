package com.ritsard.baisard.jwt.redis;

import com.ritsard.baisard.jwt.dto.login.MemberCacheDto;
import com.ritsard.baisard.jwt.model.entity.BaseMember;
import io.micrometer.common.lang.Nullable;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
public class MemberRedisService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(MemberRedisService.class);

    @Autowired(
            required = false
    )
    @Nullable
    private RedisTemplate<String, Object> redisTemplate;
    private static final String PREFIX = "member:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(30L);

    public void save(BaseMember member) {
        if (this.redisTemplate == null) {
            log.debug("[Disable Redis] Skip cache storage: {}", member.getUuidMember());
        } else {
            String key = "member:" + member.getUuidMember().toString();
            MemberCacheDto cacheDto = MemberCacheDto.from(member);
            this.redisTemplate.opsForValue().set(key, cacheDto, CACHE_TTL);
            log.debug("[Redis save] key={}, TTL={}s", key, CACHE_TTL.getSeconds());
        }
    }

    public Optional<MemberCacheDto> get(UUID memberUuid) {
        if (this.redisTemplate == null) {
            log.debug("[Redis Disabled] Skip cache lookup\n: {}", memberUuid);
            return Optional.empty();
        } else {
            String key = "member:" + memberUuid.toString();
            Object cached = this.redisTemplate.opsForValue().get(key);
            return Optional.ofNullable((MemberCacheDto) cached);
        }
    }

    public void delete(UUID memberUuid) {
        if (this.redisTemplate == null) {
            log.debug("[Redis Disabled] Skip cache deletion: {}", memberUuid);
        } else {
            String key = "member:" + memberUuid.toString();
            this.redisTemplate.delete(key);
            log.debug("[Redis delete] key={}", key);
        }
    }
}

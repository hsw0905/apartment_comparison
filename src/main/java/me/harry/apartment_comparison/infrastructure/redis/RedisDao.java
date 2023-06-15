package me.harry.apartment_comparison.infrastructure.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Component
public class RedisDao {
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisDao(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isRedisReady() {
        return redisTemplate.getRequiredConnectionFactory().getConnection().ping() != null;
    }

    public Object findByKey(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void setValueWithExpireTime(String key, Object value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    // NullPointerException 주의
    public void delete(Collection<String> keys) {
        redisTemplate.delete(keys);
    }
}

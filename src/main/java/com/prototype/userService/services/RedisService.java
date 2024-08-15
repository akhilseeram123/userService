package com.prototype.userService.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public <V> V get(String key, Class<V> entityClass) {
        Object o = redisTemplate.opsForValue().get(key);
        if(o != null){
            return entityClass.cast(o);
        }
        return null;
    }

    public <V> void set(String key, V entityClass, Long ttl) {
        redisTemplate.opsForValue().set(key, entityClass, ttl, TimeUnit.SECONDS);
    }

    public <V> V getAndDelete(String key, Class<V> entityClass){
        Object o = redisTemplate.opsForValue().getAndDelete(key);
        if(o != null){
            return entityClass.cast(o);
        }
        return null;
    }
}

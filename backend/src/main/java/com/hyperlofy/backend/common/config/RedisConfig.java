package com.hyperlofy.backend.common.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Enterprise Redis Configuration for low-latency session validation,
 * geofencing operations, idempotency lock registries, and domain caching.
 */
@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // Use String serialization for keys
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        
        // Use Jackson2 Json serialization for values to support dynamic object marshaling
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(15))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("platform_configurations", defaultCacheConfig.entryTtl(Duration.ofMinutes(60)));
        cacheConfigurations.put("feature_flags", defaultCacheConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put("merchant_profiles", defaultCacheConfig.entryTtl(Duration.ofMinutes(15)));
        cacheConfigurations.put("customer_home_feed", defaultCacheConfig.entryTtl(Duration.ofMinutes(10)));
        cacheConfigurations.put("inventory_items", defaultCacheConfig.entryTtl(Duration.ofMinutes(5)));
        cacheConfigurations.put("coupons", defaultCacheConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put("dashboards", defaultCacheConfig.entryTtl(Duration.ofMinutes(1)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultCacheConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}

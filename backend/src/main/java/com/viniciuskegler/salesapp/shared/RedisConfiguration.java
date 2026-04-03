package com.viniciuskegler.salesapp.shared;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfiguration {

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(1))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(RedisSerializer.json()));
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory,
                                     RedisCacheConfiguration defaults,
                                     List<Map<String, RedisCacheConfiguration>> cacheConfigurations) {
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaults);
        cacheConfigurations.forEach(map -> map.forEach(builder::withCacheConfiguration));
        return builder.build();
    }

}

package com.viniciuskegler.salesapp.product;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

import java.time.Duration;
import java.util.Map;

@Configuration
public class ProductCacheConfiguration {

    @Bean
    public Map<String, RedisCacheConfiguration> productCacheConfigurations(RedisCacheConfiguration defaults) {
        return Map.of(
                "product-categories", defaults.entryTtl(Duration.ofMinutes(30)),
                "product-details", defaults.entryTtl(Duration.ofMinutes(10)),
                "related-products", defaults.entryTtl(Duration.ofMinutes(10))
        );
    }

}

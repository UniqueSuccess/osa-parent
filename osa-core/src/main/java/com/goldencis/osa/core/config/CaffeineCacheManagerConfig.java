package com.goldencis.osa.core.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by limingchao on 2018/9/29.
 */
@Configuration
@EnableCaching
public class CaffeineCacheManagerConfig {

    @Value("${spring.cache.caffeine.spec}")
    private String spec;

    @Bean
    public CacheManager caffeineCacheManager() {

//        Caffeine caffeine = Caffeine.newBuilder().
                //cache的初始容量值
//                .initialCapacity(100)
                //maximumSize用来控制cache的最大缓存数量，maximumSize和maximumWeight不可以同时使用，
//                .maximumSize(1000)
        //控制最大权重
//                .maximumWeight(100);
//                .expireAfter();
//                .expireAfterWrite(50, TimeUnit.SECONDS);
        //使用refreshAfterWrite必须要设置cacheLoader
//                .refreshAfterWrite(5,TimeUnit.SECONDS);
        Caffeine caffeine = Caffeine.from(spec);

        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setAllowNullValues(true);
        cacheManager.setCaffeine(caffeine);
//        cacheManager.setCacheLoader(cacheLoader);
        return cacheManager;
    }

}

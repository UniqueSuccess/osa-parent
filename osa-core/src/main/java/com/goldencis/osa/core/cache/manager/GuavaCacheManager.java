package com.goldencis.osa.core.cache.manager;

/**
 * Created by limingchao on 2018/9/28.
 */
import com.goldencis.osa.core.cache.GuavaCache;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;

//@Component
public class GuavaCacheManager extends AbstractTransactionSupportingCacheManager {
    private Collection<GuavaCache> caches;

    private String spec;

    private volatile CacheBuilder<Object, Object> cacheBuilder;

    @Value("${guava.cacheSize}")
    private Long cacheSize;
    @Value("${guava.timeLength}")
    private Long timeLength;

    private boolean allowNullValues = true;

    public GuavaCacheManager() {
    }

    public void setCaches(Collection<GuavaCache> caches) {
        this.caches = caches;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getSpec() {
        return spec;
    }

    public void setAllowNullValues(boolean allowNullValues) {
        this.allowNullValues = allowNullValues;
    }

    public boolean isAllowNullValues() {
        return allowNullValues;
    }

    @Override
    protected Collection<? extends Cache> loadCaches() {
        return (caches != null) ? caches : Collections.<GuavaCache> emptyList();
    }

    @Override
    public Cache getCache(String name) {
        Cache cache = super.getCache(name);
        return cache;
    }

    private GuavaCache createGuavaCache(String name) {
        if (StringUtils.hasText(spec)) {
            return new GuavaCache(name, getCacheBuilder(), allowNullValues, this.cacheSize, this.timeLength);
        } else {
            return new GuavaCache(name, allowNullValues, this.cacheSize, this.timeLength);
        }
    }

    private CacheBuilder<Object, Object> getCacheBuilder() {
        if (cacheBuilder == null) {
            synchronized (this) {
                if (cacheBuilder == null) {
                    if (StringUtils.hasText(spec)) {
                        cacheBuilder = CacheBuilder.from(spec);
                    } else {
                        cacheBuilder = CacheBuilder.newBuilder();
                    }
                }
                notify();
            }
        }

        return cacheBuilder;
    }

    public Long getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(Long cacheSize) {
        this.cacheSize = cacheSize;
    }

    public Long getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(Long timeLength) {
        this.timeLength = timeLength;
    }

    public Cache getMissingCache(String name) {
        return createGuavaCache(name);
    }

}

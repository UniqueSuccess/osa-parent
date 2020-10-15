package com.goldencis.osa.core.cache;

/**
 * Created by limingchao on 2018/9/28.
 */
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheBuilderSpec;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

public class GuavaCache implements Cache {

    private static final Object NULL_HOLDER = new NullHolder();

    private final String name;

    private final com.google.common.cache.Cache<Object, Object> store;

    private final boolean allowNullValues;

    public GuavaCache(String name, Long cacheSize, Long timeLength) {
        this(name, CacheBuilder.newBuilder(), true, cacheSize, timeLength);
    }

    public GuavaCache(String name, boolean allowNullValues, Long cacheSize, Long timeLength) {
        this(name, CacheBuilder.newBuilder(), allowNullValues, cacheSize, timeLength);
    }

    public GuavaCache(String name, CacheBuilderSpec spec, boolean allowNullValues, Long cacheSize, Long timeLength) {
        this(name, CacheBuilder.from(spec), allowNullValues, cacheSize, timeLength);
    }

    public GuavaCache(String name, CacheBuilder<Object, Object> builder, boolean allowNullValues, Long cacheSize,
                      Long timeLength) {
        this.name = checkNotNull(name, "name is required");
        this.allowNullValues = allowNullValues;
        this.store = builder.maximumSize(cacheSize).expireAfterWrite(timeLength, TimeUnit.MINUTES).build();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public com.google.common.cache.Cache<Object, Object> getNativeCache() {
        return this.store;
    }

    @Override
    public ValueWrapper get(Object key) {
        Object value = this.store.getIfPresent(key);
        return (value != null ? new SimpleValueWrapper(fromStoreValue(value)) : null);
    }

    @Override
    public void put(Object key, Object value) {
        this.store.put(key, value);
    }

    @Override
    public void evict(Object key) {
        this.store.invalidate(key);
    }

    @Override
    public void clear() {
        this.store.invalidateAll();
    }

    protected Object fromStoreValue(Object storeValue) {
        if (this.allowNullValues && storeValue == NULL_HOLDER) {
            return null;
        }
        return storeValue;
    }

    protected Object toStoreValue(Object userValue) {
        if (this.allowNullValues && userValue == null) {
            return NULL_HOLDER;
        }
        return userValue;
    }

    @SuppressWarnings("serial")
    private static class NullHolder implements Serializable {

    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return null;
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        // TODO Auto-generated method stub
        return null;
    }
}

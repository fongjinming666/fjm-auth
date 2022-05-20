package com.fjm.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface RedisService {

    void set(String key, String value);

    void set(String key, String value, long time);

    void set(String key, String value, long time, TimeUnit timeUnit);

    String get(String key);

    String getString(String key);

    Long incr(String key);

    Long incr(String key, long delta);

    Long decr(String key, long delta);

    void addSet(String key, String value);

    Set<String> getSet(String key);

    void deleteSet(String key, Object values);

    Object hGet(String key, Object hashKey);

    void hSet(String key, Object hashKey, Object value);

    Boolean hSet(String key, String hashKey, Object value, long time);

    Boolean del(String key);

    Long del(List<String> keys);

    Boolean expire(String key, long time);

    void expire(String key, long time, TimeUnit timeUnit);

    Set<String> scan(String matchKey);

    Set<String> scanWithoutWildcard(String matchKey);

    void convertAndSend(String key, String value);

    List<Object> getStringCollectionByPipelined(Collection<String> collection);
}

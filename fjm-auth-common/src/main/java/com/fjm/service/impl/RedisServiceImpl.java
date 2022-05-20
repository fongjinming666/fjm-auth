package com.fjm.service.impl;

import com.fjm.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.*;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisServiceImpl implements RedisService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void set(String key, String value, long secondTimes) {
        redisTemplate.opsForValue().set(key, value, secondTimes, TimeUnit.SECONDS);
    }

    @Override
    public void set(String key, String value, long time, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, time, timeUnit);
    }

    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public String getString(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value == null ? "" : value.toString();
    }

    @Override
    public Long incr(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    @Override
    public Long incr(String key, long delta) {
        return this.redisTemplate.opsForValue().increment(key, delta);
    }

    @Override
    public Long decr(String key, long delta) {
        return this.redisTemplate.opsForValue().increment(key, -delta);
    }

    @Override
    public void addSet(String key, String value) {
        redisTemplate.opsForSet().add(key, value);
    }

    @Override
    public Set<String> getSet(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    @Override
    public void deleteSet(String key, Object values) {
        redisTemplate.opsForSet().remove(key, values);
    }

    @Override
    public Object hGet(String key, Object hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    @Override
    public void hSet(String key, Object hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    @Override
    public Boolean hSet(String key, String hashKey, Object value, long time) {
        this.redisTemplate.opsForHash().put(key, hashKey, value);
        return this.expire(key, time);
    }

    @Override
    public Boolean del(String key) {
        return this.redisTemplate.delete(key);
    }

    @Override
    public Long del(List<String> keys) {
        return this.redisTemplate.delete(keys);
    }

    @Override
    public Boolean expire(String key, long time) {
        return this.redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    @Override
    public void expire(String key, long time, TimeUnit timeUnit) {
        redisTemplate.boundValueOps(key).expire(time, timeUnit);
    }

    @Override
    public Set<String> scan(String matchKey) {
        Set<String> keys = redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keysTmp = new HashSet<>();
            Cursor<byte[]> cursor = connection.scan(new ScanOptions.ScanOptionsBuilder().match("*" + matchKey + "*").count(10000).build());
            while (cursor.hasNext()) {
                keysTmp.add(new String(cursor.next()));
            }
            return keysTmp;
        });

        return keys;
    }

    /**
     * 模糊查询，但是不再在前后加通配符
     *
     * @param matchKey 匹配字符
     * @return 结果
     */
    @Override
    public Set<String> scanWithoutWildcard(String matchKey) {
        Set<String> keys = redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keysTmp = new HashSet<>();
            long t1 = System.currentTimeMillis();
            Cursor<byte[]> cursor = connection.scan(new ScanOptions.ScanOptionsBuilder().match(matchKey).count(10000).build());
            long t2 = System.currentTimeMillis();
            int i = 1;
            while (cursor.hasNext()) {
                keysTmp.add(new String(cursor.next()));
                i++;
            }
            long t3 = System.currentTimeMillis();
            log.info("matchKey:{},i={},t1:{},t2-t1:{},t3-t2:{},all:{}", matchKey, i, t1, t2 - t1, t3 - t2, t3 - t1);
            return keysTmp;
        });

        return keys;
    }

    @Override
    public void convertAndSend(String key, String value) {
        redisTemplate.convertAndSend(key, value);
    }

    @Override
    public List<Object> getStringCollectionByPipelined(Collection<String> collection) {
        return stringRedisTemplate.executePipelined((RedisCallback<String>) connection -> {
            StringRedisConnection conn = (StringRedisConnection) connection;
            for (String key : collection) {
                conn.get(key);
            }
            return null;
        });
    }
}

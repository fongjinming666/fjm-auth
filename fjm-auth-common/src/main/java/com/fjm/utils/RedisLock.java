package com.fjm.utils;/**
 * @Author: jinmingfong
 * @CreateTime: 2022/5/18 09:13
 * @Description:
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author: jinmingfong
 * @CreateTime: 2022-05-18 09:13
 * @Description: Redis 分布式锁。 基于 springboot2.x ，内置的Redis客户端 lettuce，（非Jedis)
 */
@Slf4j
@Component
@Lazy
public class RedisLock {

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 保存锁的value
     */
    private ThreadLocal<String> threadLocal = new ThreadLocal<>();

    /**
     * 释放锁脚本，原子操作，lua脚本
     */
    private static final String UNLOCK_LUA;
    /**
     * 默认过期时间(30ms)
     */
    private static final long DEFAULT_EXPIRE = 30L;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
        sb.append("then ");
        sb.append("    return redis.call(\"del\",KEYS[1]) ");
        sb.append("else ");
        sb.append("    return 0 ");
        sb.append("end ");
        UNLOCK_LUA = sb.toString();
    }

    /**
     * 获取分布式锁，原子操作
     *
     * @param lockKey   锁
     * @param lockValue 唯一ID, 可以使用UUID.randomUUID().toString();
     * @return 是否枷锁成功
     */
    public boolean lock(String lockKey, String lockValue) {
        return this.lock(lockKey, lockValue, DEFAULT_EXPIRE, TimeUnit.MILLISECONDS);
    }

    /**
     * 获取分布式锁，原子操作
     *
     * @param lockKey   锁
     * @param lockValue 唯一ID, 可以使用UUID.randomUUID().toString();
     * @param expire    过期时间
     * @param timeUnit  时间单位
     * @return 是否枷锁成功
     */
    public boolean lock(String lockKey, String lockValue, long expire, TimeUnit timeUnit) {
        try {
            RedisCallback callback = (connection) -> connection.set(lockKey.getBytes(StandardCharsets.UTF_8),
                    lockValue.getBytes(StandardCharsets.UTF_8), Expiration.seconds(timeUnit.toSeconds(expire)),
                    RedisStringCommands.SetOption.SET_IF_ABSENT);
            return (boolean) redisTemplate.execute(callback);
        } catch (Exception e) {
            log.error("redis lock error ,lock key: {}, value : {}, error info : {}", lockKey, lockValue, e);
        }
        return false;
    }

    /**
     * @param key            锁key
     * @param requestId      加锁唯一标识
     * @param lockTimeOut    加锁超时时间
     * @param timeUnit       类型
     * @param requireTimeOut 加锁总超时时间
     * @param retries        重试次数
     * @return boolean
     * @Author hw
     * @Description 可重入锁
     * @date 2021-7-13 18:16
     */
    public boolean tryLock(String key, String requestId, long lockTimeOut, TimeUnit timeUnit, long requireTimeOut, int retries) {
        //可重入锁判断
        String originValue = threadLocal.get();
        if (!StringUtils.isEmpty(originValue) && isReentrantLock(key, originValue)) {
            return true;
        }
//		String value = UUID.randomUUID().toString();
        long end = System.currentTimeMillis() + requireTimeOut;
        int retryTimes = 1;

        try {
            while (System.currentTimeMillis() < end) {
                if (retryTimes > retries) {
                    log.error(" require lock failed,retry times [{}]", retries);
                    return false;
                }
                if (lock(key, requestId, lockTimeOut, timeUnit)) {
                    threadLocal.set(requestId);
                    return true;
                }
                // 休眠10ms
                Thread.sleep(100);
                retryTimes++;
            }
        } catch (Exception e) {
            log.error("tryLock Exception", e);
        }
        return false;
    }

    /**
     * 是否为重入锁
     */
    private boolean isReentrantLock(String key, String originValue) {
        String v = (String) redisTemplate.opsForValue().get(key);
        return v != null && originValue.equals(v);
    }

    /**
     * 释放锁
     *
     * @param lockKey   锁
     * @param lockValue 唯一ID
     * @return 执行结果
     */
    public boolean unlock(String lockKey, String lockValue) {
        RedisCallback callback = (connection) -> connection.eval(UNLOCK_LUA.getBytes(), ReturnType.BOOLEAN, 1, lockKey.getBytes(StandardCharsets.UTF_8), lockValue.getBytes(StandardCharsets.UTF_8));
        return (boolean) redisTemplate.execute(callback);
    }

    /**
     * 获取Redis锁的value值
     *
     * @param lockKey 锁
     */
    public String get(String lockKey) {
        try {
            RedisCallback callback = (connection) -> new String(Objects.requireNonNull(connection.get(lockKey.getBytes())), StandardCharsets.UTF_8);
            return redisTemplate.execute(callback).toString();
        } catch (Exception e) {
            log.error("get redis value occurred an exception,the key is {}, error is {}", lockKey, e);
        }
        return null;
    }
}

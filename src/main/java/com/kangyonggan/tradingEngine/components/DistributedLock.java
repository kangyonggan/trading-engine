package com.kangyonggan.tradingEngine.components;

import com.kangyonggan.tradingEngine.constants.enums.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author kyg
 */
@FunctionalInterface
public interface DistributedLock {

    Logger LOGGER = LoggerFactory.getLogger(DistributedLock.class);

    /**
     * 超时时间（单位秒）
     */
    Long TIMEOUT = 10L;

    /**
     * 重试间隔（单位毫秒）
     */
    Long RETRY_INTERVAL = 200L;

    /**
     * 获取锁从成功的回调
     */
    void onSuccess();

    /**
     * 获取锁
     *
     * @param lockId
     */
    default void getLock(String lockId) {
        getLock(lockId, TIMEOUT);
    }

    /**
     * 获取锁
     *
     * @param lockId
     * @param timeout
     */
    default void getLock(String lockId, Long timeout) {
        RedisManager redisManager = SpringContext.getBean(RedisManager.class);
        boolean getLock = false;
        try {
            if (getRedisLock(redisManager, lockId, timeout)) {
                getLock = true;
                onSuccess();
                return;
            }
        } catch (RuntimeException e) {
            // 业务异常，抛出去
            throw e;
        } catch (Exception e) {
            LOGGER.error("获取分布式锁异常，key={}", lockId, e);
            Thread.currentThread().interrupt();
        } finally {
            if (getLock) {
                redisManager.delete(lockId);
            }
        }
        onTimeout();
    }

    /**
     * 超时
     */
    default void onTimeout() {
        LOGGER.warn("获取分布式锁超时");
        throw new BizException(ErrorCode.TIMEOUT);
    }

    /**
     * 获取redis锁
     *
     * @param redisManager
     * @param lockId
     * @param timeout
     * @return
     */
    default boolean getRedisLock(RedisManager redisManager, String lockId, Long timeout) {
        long startTime = System.currentTimeMillis();
        while (!redisManager.getLock(lockId)) {
            if (System.currentTimeMillis() - startTime > timeout) {
                return false;
            }
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(RETRY_INTERVAL));
        }
        return true;
    }


}

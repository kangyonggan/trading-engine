package com.kangyonggan.tradingEngine.components;

import com.kangyonggan.tradingEngine.constants.RedisKeys;
import com.kangyonggan.tradingEngine.dto.TickDto;
import com.kangyonggan.tradingEngine.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author longjie
 * @since 8/14/18
 */
@Component
public class RedisManager {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * set
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
        return true;
    }

    /**
     * set
     *
     * @param key
     * @param value
     * @param value
     * @return
     */
    public boolean set(String key, Object value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
        return true;
    }

    /**
     * set
     *
     * @param key
     * @param value
     * @param timeout
     * @param unit
     * @return
     */
    public boolean set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
        return true;
    }

    /**
     * get
     *
     * @param key
     * @return
     */
    public Object getObject(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * get
     *
     * @param key
     * @return
     */
    public <T> T get(String key) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    /**
     * getAndRefresh
     *
     * @param key
     * @param timeout
     * @return
     */
    public <T> T getAndRefresh(String key, long timeout) {
        T t = (T) redisTemplate.opsForValue().get(key);
        if (t != null) {
            redisTemplate.expire(key, timeout, TimeUnit.MINUTES);
        }
        return t;
    }

    /**
     * get
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public <T> T get(String key, T defaultValue) {
        T val = (T) redisTemplate.opsForValue().get(key);
        return val == null ? defaultValue : val;
    }

    /**
     * ?????????
     *
     * @param key ???
     * @return ??????????????????
     */
    public boolean getLock(String key) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "1");
        return success != null && success;
    }

    /**
     * ?????????
     *
     * @param key    ???
     * @param expire ?????????????????????
     * @return ??????????????????
     */
    public boolean getLock(String key, long expire) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "1", expire, TimeUnit.SECONDS);
        return success != null && success;
    }

    /**
     * ??????
     *
     * @param channel
     * @param msg
     */
    public void pubTopic(String channel, Object msg) {
        redisTemplate.convertAndSend(channel, msg);
    }

    /**
     * delete
     *
     * @param key
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * delete all like pattern
     *
     * @param pattern
     * @return
     */
    public void deleteAll(String pattern) {
        redisTemplate.delete(keys(pattern));
    }

    /**
     * increment
     *
     * @param key
     * @return
     */
    public long increment(String key) {
        return redisTemplate.opsForValue().increment(key, 1);
    }

    /**
     * ?????????????????????K???
     *
     * @param symbol
     * @return
     */
    public TickDto getLastKline(String symbol) {
        return (TickDto) redisTemplate.opsForValue().get("LAST_KLINE:" + symbol);
    }

    /**
     * ???????????????K???
     *
     * @param symbol
     * @param min
     * @param max
     * @return
     */
    public TickDto getKline(String symbol, Long min, Long max) {
        Set<Object> tickDtos = redisTemplate.opsForZSet().rangeByScore("KLINE_" + symbol, min, max);
        return tickDtos == null || tickDtos.isEmpty() ? null : (TickDto) tickDtos.toArray()[0];
    }

    /**
     * ??????K???
     *
     * @param tickDto
     */
    public void addKline(TickDto tickDto) {
        redisTemplate.opsForZSet().removeRangeByScore("KLINE_" + tickDto.getSymbol(), tickDto.getId(), tickDto.getId());
        redisTemplate.opsForZSet().add("KLINE_" + tickDto.getSymbol(), tickDto, tickDto.getId());
        redisTemplate.opsForValue().set("LAST_KLINE:" + tickDto.getSymbol(), tickDto);
    }

    /**
     * get keys of pattern
     *
     * @param pattern
     * @return
     */
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * ?????????????????????
     *
     * @return
     */
    public String getIncrSerialNo() {
        return getIncrSerialNo(null);
    }

    /**
     * ?????????????????????
     *
     * @param prefix
     * @return
     */
    public String getIncrSerialNo(String prefix) {
        String nextVal = String.valueOf(increment(RedisKeys.INCR) % 1000000);
        String currentDate = DateUtil.format(new Date(), DateUtil.DATETIME14_PATTERN);

        return (prefix == null ? "" : prefix) + currentDate + StringUtils.leftPad(nextVal, 6, "0");
    }

    /**
     * ?????????????????????
     *
     * @return
     */
    public String getRandSerialNo() {
        String nextVal = String.valueOf(increment(RedisKeys.INCR) % 10000);
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16)
                + StringUtils.leftPad(nextVal, 4, "0");
    }
}

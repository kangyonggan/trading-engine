package com.kangyonggan.tradingEngine.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.LinkedHashMap;

/**
 * @author kyg
 */
@Data
public class RequestParams<K, V> extends LinkedHashMap<K, V> {

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}

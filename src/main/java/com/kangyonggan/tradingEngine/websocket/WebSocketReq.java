package com.kangyonggan.tradingEngine.websocket;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author kyg
 */
@Data
public class WebSocketReq implements Serializable {

    private String method;
    private String params;

    public String getInterval() {
        return params.substring(params.indexOf("_") + 1);
    }

    public String getSymbol() {
        if (params.contains("_")) {
            return params.substring(params.indexOf("@") + 1, params.lastIndexOf("_"));
        }
        return params.substring(params.indexOf("@") + 1);
    }

}

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
    private List<String> params;

}

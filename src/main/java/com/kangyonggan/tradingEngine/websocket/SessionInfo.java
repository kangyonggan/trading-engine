package com.kangyonggan.tradingEngine.websocket;

import lombok.Data;

import javax.websocket.Session;
import java.io.Serializable;

/**
 * @author kyg
 */
@Data
public class SessionInfo implements Serializable {

    public SessionInfo(Session session) {
        this.session = session;
    }

    private Session session;

    private String symbol;

    private String interval;

    private Long expireTime = System.currentTimeMillis() + 180000;

    public void updateExpireTime() {
        this.expireTime = System.currentTimeMillis() + 180000;
    }
}

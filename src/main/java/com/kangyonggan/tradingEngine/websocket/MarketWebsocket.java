package com.kangyonggan.tradingEngine.websocket;

import com.alibaba.fastjson.JSONObject;
import com.kangyonggan.tradingEngine.components.RedisManager;
import com.kangyonggan.tradingEngine.service.ITradeService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author kyg
 */
@ServerEndpoint("/ws/market")
@Component
@Log4j2
public class MarketWebsocket {

    /**
     * 存放所有的客户端
     */
    private static final Map<String, SessionInfo> CLIENTS = new ConcurrentHashMap<>();

    private static final Map<String, Long> INTERVAL_LEN = new HashMap<>();

    static {
        INTERVAL_LEN.put("1min", 1440L);
        INTERVAL_LEN.put("5min", 500L);
        INTERVAL_LEN.put("15min", 500L);
        INTERVAL_LEN.put("30min", 200L);
        INTERVAL_LEN.put("60min", 240L);
        INTERVAL_LEN.put("4hour", 200L);
        INTERVAL_LEN.put("1day", 200L);
        INTERVAL_LEN.put("1week", 200L);
        INTERVAL_LEN.put("1month", 200L);
        INTERVAL_LEN.put("1year", 200L);
    }

    @Autowired
    private ITradeService tradeService;

    @Autowired
    private RedisManager redisManager;

    @PostConstruct
    public void init() {
        sendHeartBeat();
    }

    /**
     * 新建链接
     *
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) {
        log.info("有新的客户端连接了: {}", session.getId());
        CLIENTS.put(session.getId(), new SessionInfo(session));
    }

    /**
     * 发送心跳
     */
    private void sendHeartBeat() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(15000);
                    log.info("在线人数：{}", CLIENTS.size());
                    for (SessionInfo sessionInfo : CLIENTS.values()) {
                        if (sessionInfo.getExpireTime() < System.currentTimeMillis()) {
                            sessionInfo.getSession().close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "服务器长时间未收到心跳回复"));
                        } else {
                            JSONObject params = new JSONObject();
                            params.put("PING", System.currentTimeMillis());
                            sessionInfo.getSession().getAsyncRemote().sendText(params.toJSONString());
                        }
                    }
                } catch (Exception e) {
                    log.error("心跳线程异常", e);
                }
            }
        }).start();
    }

    /**
     * 发生错误
     *
     * @param throwable
     */
    @OnError
    public void onError(Throwable throwable) {
        log.error("websocket发生错误", throwable);
    }

    /**
     * 链接关闭
     *
     * @param session session
     */
    @OnClose
    public void onClose(Session session) {
        log.info("有用户断开了, id为:{}", session.getId());
        CLIENTS.remove(session.getId());
    }

    /**
     * 收到客户端发来消息
     *
     * @param session 会话
     * @param message 消息对象
     */
    @OnMessage
    public void onMessage(Session session, String message) {
        log.info("websocket收到客户端发来的消息: {}", message);
        WebSocketReq req = JSONObject.parseObject(message, WebSocketReq.class);
        if (req.getMethod().equals(WebSocketMethod.SUB.name())) {
            CLIENTS.get(session.getId()).addTopic(req.getParams());
        } else if (req.getMethod().equals(WebSocketMethod.UNSUB.name())) {
            CLIENTS.get(session.getId()).removeTopic(req.getParams());
        } else if (req.getMethod().equals(WebSocketMethod.PONG.name())) {
            CLIENTS.get(session.getId()).updateExpireTime();
        } else if (req.getMethod().equals(WebSocketMethod.REQ.name())) {
            if (req.getParams().startsWith("KLINE@")) {
                // 请求历史K线，KLINE@BTCUSDT_1min
                JSONObject msg = new JSONObject();
                msg.put("channel", req.getParams());
                Long size = INTERVAL_LEN.getOrDefault(req.getInterval(), 300L);
                long max = System.currentTimeMillis() / 60000;
                msg.put("result", redisManager.getKline(req.getSymbol(), max - size, max));
                session.getAsyncRemote().sendText(msg.toJSONString());
            } else if (req.getParams().startsWith("TRADE@")) {
                // 请求最新成交，TRADE@BTCUSDT
                JSONObject msg = new JSONObject();
                msg.put("channel", req.getParams());
                msg.put("result", tradeService.getLast30Trade(req.getSymbol()));
                session.getAsyncRemote().sendText(msg.toJSONString());
            }
        }
    }

    /**
     * 发送消息
     *
     * @param topic 话题
     * @param msg   消息内容
     */
    public void sendMsg(String topic, String msg) {
        for (Map.Entry<String, SessionInfo> sessionEntry : CLIENTS.entrySet()) {
            if (sessionEntry.getValue().containsTopic(topic)) {
                sessionEntry.getValue().getSession().getAsyncRemote().sendText(msg);
            }
        }
    }
}

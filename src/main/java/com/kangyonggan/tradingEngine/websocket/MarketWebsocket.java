package com.kangyonggan.tradingEngine.websocket;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
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
            CLIENTS.get(session.getId()).addTopics(req.getParams());
        } else if (req.getMethod().equals(WebSocketMethod.UNSUB.name())) {
            CLIENTS.get(session.getId()).removeTopics(req.getParams());
        } else if (req.getMethod().equals(WebSocketMethod.REQ.name())) {
            // TODO 返回请求数据
        } else if (req.getMethod().equals(WebSocketMethod.PONG.name())) {
            CLIENTS.get(session.getId()).updateExpireTime();
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

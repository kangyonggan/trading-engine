package com.kangyonggan.tradingEngine.websocket;

import lombok.Data;

import javax.websocket.Session;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author kyg
 */
@Data
public class SessionInfo implements Serializable {

    public SessionInfo(Session session) {
        this.session = session;
    }

    private Session session;

    private Set<String> topics = new HashSet<>();

    private Long expireTime = System.currentTimeMillis() + 180000;

    public void addTopics(List<String> topics) {
        this.topics.addAll(topics);
    }

    public void removeTopics(List<String> topics) {
        this.topics.removeAll(topics);
    }

    public boolean containsTopic(String topic) {
        return this.topics.contains(topic);
    }

    public void updateExpireTime() {
        this.expireTime = System.currentTimeMillis() + 180000;
    }
}

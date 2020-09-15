package com.github.booster.core.consumer;

/**
 * 订阅信息
 *
 * @author Yujiumin
 * @version 2020/09/13
 */
public class Subscription {

    private String topic;

    private String[] tags;

    public Subscription(String topic, String... tags) {
        this.topic = topic;
        this.tags = tags;
    }

    public String getTopic() {
        return topic;
    }

    public String[] getTags() {
        return tags;
    }
}

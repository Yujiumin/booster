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

    public Subscription(){

    }

    public Subscription(String topic, String... tags) {
        this.topic = topic;
        this.tags = tags;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String[] getTags() {
        return tags;
    }
}

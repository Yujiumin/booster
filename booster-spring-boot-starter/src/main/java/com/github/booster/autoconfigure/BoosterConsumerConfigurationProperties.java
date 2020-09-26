package com.github.booster.autoconfigure;

import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 消费者配置
 *
 * @author Yujiumin
 * @version 2020/09/25
 */
@Configuration
@ConfigurationProperties("booster.consumer")
public class BoosterConsumerConfigurationProperties {

    private Boolean enabled = false;

    private Integer consumeThreadMin = 10;

    private Integer consumeThreadMax = 20;

    private MessageModel messageModel = MessageModel.CLUSTERING;

    private ConsumeFromWhere consumeFromWhere = ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getConsumeThreadMin() {
        return consumeThreadMin;
    }

    public void setConsumeThreadMin(Integer consumeThreadMin) {
        this.consumeThreadMin = consumeThreadMin;
    }

    public Integer getConsumeThreadMax() {
        return consumeThreadMax;
    }

    public void setConsumeThreadMax(Integer consumeThreadMax) {
        this.consumeThreadMax = consumeThreadMax;
    }

    public MessageModel getMessageModel() {
        return messageModel;
    }

    public void setMessageModel(MessageModel messageModel) {
        this.messageModel = messageModel;
    }

    public ConsumeFromWhere getConsumeFromWhere() {
        return consumeFromWhere;
    }

    public void setConsumeFromWhere(ConsumeFromWhere consumeFromWhere) {
        this.consumeFromWhere = consumeFromWhere;
    }
}

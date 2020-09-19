package com.github.booster.core.consumer;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * 消息处理器接口
 *
 * @author Yujiumin
 * @version 2020/09/13
 */
public interface MessageHandler {

    /**
     * 消费消息
     *
     * @param messageExt 消息
     * @return 消费结果
     */
    ConsumeConcurrentlyStatus consumeMessage(MessageExt messageExt);

}

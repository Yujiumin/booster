package com.github.booster.core.consumer;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageExt;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 抽象的消息处理器，将消息提取关键信息后，再交给用户自定义的MessageHandler处理
 * 讲道理, 用户自定义的消息处理器都应该继承这个类
 *
 * @author Yujiumin
 * @version 2020/09/19
 */
public abstract class AbstractMessageHandler implements MessageHandler {

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(MessageExt messageExt) {
        String messageExtKeys = messageExt.getKeys();
        String[] keys = null;
        if (!Objects.isNull(messageExtKeys)) {
            keys = messageExtKeys.split(MessageConst.KEY_SEPARATOR);
        }
        String bodyString = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        return consumeMessage(messageExt.getMsgId(), keys, bodyString);
    }

    /**
     * 子类必须实现的方法
     *
     * @param messageId 消息ID
     * @param keys      消息发送时设置的关键字
     * @param body      消息体
     * @return 消费结果
     */
    protected abstract ConsumeConcurrentlyStatus consumeMessage(String messageId, String[] keys, String body);
}

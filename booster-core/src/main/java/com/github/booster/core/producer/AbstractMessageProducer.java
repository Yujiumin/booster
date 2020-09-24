package com.github.booster.core.producer;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;

/**
 * @author Yujiumin
 * @version 2020/09/20
 */
public abstract class AbstractMessageProducer implements SendCallback {

    @Override
    public void onSuccess(SendResult sendResult) {
        String messageId = sendResult.getMsgId();
        SendStatus sendStatus = sendResult.getSendStatus();
        this.onSuccess(messageId, sendStatus);
    }

    /**
     * 发送成功后的回调方法
     *
     * @param messageId  消息ID
     * @param sendStatus 发送状态
     */
    protected abstract void onSuccess(String messageId, SendStatus sendStatus);

}

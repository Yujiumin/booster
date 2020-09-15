package com.github.booster.core.producer;

import com.github.booster.core.builder.AbstractBuilder;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * @author Yujiumin
 * @version 2020/09/13
 */
public class Producer {

    private DefaultMQProducer producer;

    private Producer(Builder builder) {
        producer = new DefaultMQProducer(builder.groupName);
        producer.setInstanceName(builder.instanceName);
        producer.setNamesrvAddr(builder.nameServerAddr);
        producer.setRetryTimesWhenSendAsyncFailed(builder.retryTimesWhenSendAsyncFailed);
        producer.setMaxMessageSize(builder.maxMessageSize);
        producer.setVipChannelEnabled(builder.vipChannelEnabled);
    }

    public void send(Message message, SendCallback sendCallback) throws RemotingException, MQClientException, InterruptedException {
        producer.send(message, sendCallback);
    }

    static class Builder extends AbstractBuilder<Producer> {
        private String groupName;
        private String nameServerAddr;
        private String instanceName;
        private Integer retryTimesWhenSendAsyncFailed;
        private Integer maxMessageSize;
        private Boolean vipChannelEnabled;


        @Override
        public Producer build() {
            return new Producer(this);
        }
    }

}

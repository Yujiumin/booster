package com.github.booster.core.producer;

import com.github.booster.common.exception.BoosterException;
import com.github.booster.common.util.ObjectUtils;
import com.github.booster.common.util.StringUtils;
import com.github.booster.core.builder.AbstractBuilder;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.Objects;

/**
 * Booster生产者（对RocketMQ生产者进行包装）
 *
 * @author Yujiumin
 * @version 2020/09/13
 */
public class BoosterProducer {

    private DefaultMQProducer producer;

    private BoosterProducer(Builder builder) {
        producer = new DefaultMQProducer(builder.groupName);
        producer.setNamesrvAddr(builder.nameServerAddr);
        producer.setInstanceName(ObjectUtils.isNull(builder.instanceName, StringUtils.link("_", builder.groupName.toUpperCase(), "PRODUCER")));
        producer.setRetryTimesWhenSendAsyncFailed(ObjectUtils.isNull(builder.retryTimesWhenSendAsyncFailed, producer.getRetryTimesWhenSendAsyncFailed()));
        producer.setMaxMessageSize(ObjectUtils.isNull(builder.maxMessageSize, producer.getMaxMessageSize()));
        producer.setVipChannelEnabled(ObjectUtils.isNull(builder.vipChannelEnabled, producer.isVipChannelEnabled()));
    }

    public void send(Message message, SendCallback sendCallback) throws RemotingException, MQClientException, InterruptedException {
        producer.send(message, sendCallback);
    }

    static class Builder extends AbstractBuilder<BoosterProducer> {
        private String groupName;
        private String nameServerAddr;
        private String instanceName;
        private Integer retryTimesWhenSendAsyncFailed;
        private Integer maxMessageSize;
        private Boolean vipChannelEnabled;

        public Builder() {

        }

        public Builder(String groupName) {
            this.groupName = groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public void setNameServerAddr(String nameServerAddr) {
            this.nameServerAddr = nameServerAddr;
        }

        public void setInstanceName(String instanceName) {
            this.instanceName = instanceName;
        }

        public void setRetryTimesWhenSendAsyncFailed(Integer retryTimesWhenSendAsyncFailed) {
            this.retryTimesWhenSendAsyncFailed = retryTimesWhenSendAsyncFailed;
        }

        public void setMaxMessageSize(Integer maxMessageSize) {
            this.maxMessageSize = maxMessageSize;
        }

        public void setVipChannelEnabled(Boolean vipChannelEnabled) {
            this.vipChannelEnabled = vipChannelEnabled;
        }

        @Override
        public BoosterProducer build() {
            if (Objects.isNull(nameServerAddr)) {
                throw new BoosterException("NameServer地址未指定");
            } else if (Objects.isNull(groupName)) {
                throw new BoosterException("GroupName不可为空");
            } else {
                return new BoosterProducer(this);
            }
        }
    }

}

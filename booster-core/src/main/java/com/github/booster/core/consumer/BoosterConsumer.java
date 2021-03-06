package com.github.booster.core.consumer;

import com.github.booster.common.constant.BoosterConstant;
import com.github.booster.common.exception.BoosterException;
import com.github.booster.common.util.ObjectUtils;
import com.github.booster.common.util.StringUtils;
import com.github.booster.core.builder.AbstractBuilder;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Booster消费者（对RocketMQ的消费者进行包装）
 *
 * @author Yujiumin
 * @version 2020/09/13
 */
public class BoosterConsumer {

    private static Logger logger = LoggerFactory.getLogger(BoosterConsumer.class);

    private DefaultMQPushConsumer consumer;

    public BoosterConsumer(Builder builder) {
        try {
            consumer = new DefaultMQPushConsumer(builder.groupName);
            consumer.setNamesrvAddr(builder.nameServerAddr);
            consumer.setInstanceName(ObjectUtils.nullOrElse(builder.instanceName, StringUtils.link("_", "BOOSTER_CONSUMER", builder.groupName.toUpperCase())));
            consumer.setConsumeThreadMin(ObjectUtils.nullOrElse(builder.consumeThreadMin, consumer.getConsumeThreadMin()));
            consumer.setConsumeThreadMax(ObjectUtils.nullOrElse(builder.consumeThreadMax, consumer.getConsumeThreadMax()));
            consumer.setConsumeFromWhere(ObjectUtils.nullOrElse(builder.consumeFromWhere, consumer.getConsumeFromWhere()));
            consumer.setMessageModel(ObjectUtils.nullOrElse(builder.messageModel, consumer.getMessageModel()));
            consumer.registerMessageListener(new MessageDispatcher(builder.messageHandlers));

            for (Subscription subscription : builder.subscriptions) {
                String topic = subscription.getTopic();
                String[] tags = subscription.getTags();
                consumer.subscribe(topic, String.join(BoosterConstant.TAG_SEPARATOR, tags));
            }

        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        try {
            consumer.start();
            String nameServerAddr = consumer.getNamesrvAddr();
            String consumerGroup = consumer.getConsumerGroup();
            logger.info("消费者启动成功 -> [NameServerAddr: {}] [GroupName: {}]", nameServerAddr, consumerGroup);
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    public static class Builder extends AbstractBuilder<BoosterConsumer> {
        private String groupName;
        private String instanceName;
        private String nameServerAddr;
        private Integer consumeThreadMin;
        private Integer consumeThreadMax;
        private MessageModel messageModel;
        private ConsumeFromWhere consumeFromWhere;
        private Subscription[] subscriptions;
        private MessageHandler[] messageHandlers;

        public Builder() {
        }

        public Builder(String groupName) {
            this.groupName = groupName;
        }

        public Builder setGroupName(String groupName) {
            this.groupName = groupName;
            return this;
        }

        public Builder setInstanceName(String instanceName) {
            this.instanceName = instanceName;
            return this;
        }

        public Builder setNameServerAddr(String nameServerAddr) {
            this.nameServerAddr = nameServerAddr;
            return this;
        }

        public Builder setConsumeThreadMin(Integer consumeThreadMin) {
            this.consumeThreadMin = consumeThreadMin;
            return this;
        }

        public Builder setConsumeThreadMax(Integer consumeThreadMax) {
            this.consumeThreadMax = consumeThreadMax;
            return this;
        }

        public Builder setMessageModel(MessageModel messageModel) {
            this.messageModel = messageModel;
            return this;
        }

        public Builder setConsumeFromWhere(ConsumeFromWhere consumeFromWhere) {
            this.consumeFromWhere = consumeFromWhere;
            return this;
        }

        public Builder setSubscriptions(Subscription... subscriptions) {
            this.subscriptions = subscriptions;
            return this;
        }

        public Builder registerMessageHandlers(MessageHandler... messageHandlers) {
            this.messageHandlers = messageHandlers;
            return this;
        }

        @Override
        public BoosterConsumer build() {
            if (Objects.isNull(nameServerAddr)) {
                throw new BoosterException("NameServer地址未指定");
            } else if (Objects.isNull(groupName)) {
                throw new BoosterException("GroupName不可为空");
            } else if (Objects.isNull(subscriptions)) {
                throw new BoosterException("订阅信息不可为空");
            } else {
                return new BoosterConsumer(this);
            }
        }

    }
}

package com.github.booster.core.producer;

import com.github.booster.common.exception.BoosterException;
import com.github.booster.common.util.ObjectUtils;
import com.github.booster.common.util.StringUtils;
import com.github.booster.core.builder.AbstractBuilder;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Booster生产者（对RocketMQ生产者进行包装）
 *
 * @author Yujiumin
 * @version 2020/09/13
 */
public class BoosterProducer {

    private Logger logger = LoggerFactory.getLogger(BoosterProducer.class);

    private DefaultMQProducer producer;

    private BoosterProducer(Builder builder) {
        producer = new DefaultMQProducer(builder.groupName);
        producer.setNamesrvAddr(builder.nameServerAddr);
        producer.setInstanceName(ObjectUtils.nullOrElse(builder.instanceName,StringUtils.link("_", builder.groupName.toUpperCase(), "PRODUCER")));
        producer.setRetryTimesWhenSendAsyncFailed(ObjectUtils.nullOrElse(builder.retryTimesWhenSendAsyncFailed, producer.getRetryTimesWhenSendAsyncFailed()));
        producer.setMaxMessageSize(ObjectUtils.nullOrElse(builder.maxMessageSize, producer.getMaxMessageSize()));
        producer.setVipChannelEnabled(ObjectUtils.nullOrElse(builder.vipChannelEnabled, producer.isVipChannelEnabled()));
    }

    public void start() {
        try {
            producer.start();
            String producerGroup = producer.getProducerGroup();
            String nameServerAddr = producer.getNamesrvAddr();
            BoosterProducerContext.registerProducer(producerGroup, producer);
            logger.info("生产者启动成功 -> [NameServerAddr: {}] [GroupName: {}]", nameServerAddr, producerGroup);
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    public static class Builder extends AbstractBuilder<BoosterProducer> {
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

        public Builder setGroupName(String groupName) {
            this.groupName = groupName;
            return this;
        }

        public Builder setNameServerAddr(String nameServerAddr) {
            this.nameServerAddr = nameServerAddr;
            return this;
        }

        public Builder setInstanceName(String instanceName) {
            this.instanceName = instanceName;
            return this;
        }

        public Builder setRetryTimesWhenSendAsyncFailed(Integer retryTimesWhenSendAsyncFailed) {
            this.retryTimesWhenSendAsyncFailed = retryTimesWhenSendAsyncFailed;
            return this;
        }

        public Builder setVipChannelEnabled(Boolean vipChannelEnabled) {
            this.vipChannelEnabled = vipChannelEnabled;
            return this;
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

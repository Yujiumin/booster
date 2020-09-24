package com.github.booster.autoconfigure;

import com.github.booster.common.util.CollectionUtils;
import com.github.booster.core.consumer.BoosterConsumer;
import com.github.booster.core.consumer.MessageHandler;
import com.github.booster.core.consumer.Subscription;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 消费者自动配置
 *
 * @author Yujiumin
 * @version 2020/09/25
 */
@Configuration
@ConditionalOnProperty(prefix = "booster.consumer", name = "enabled", havingValue = "true")
@ComponentScan("com.github.booster.autoconfigure")
public class BoosterConsumerAutoConfiguration implements InitializingBean {

    private BoosterConfigurationProperties configurationProperties;

    private BoosterConsumerConfigurationProperties consumerConfigurationProperties;

    private List<MessageHandler> messageHandlerList;

    public BoosterConsumerAutoConfiguration(BoosterConfigurationProperties configurationProperties, BoosterConsumerConfigurationProperties consumerConfigurationProperties) {
        this.configurationProperties = configurationProperties;
        this.consumerConfigurationProperties = consumerConfigurationProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, List<MessageHandler>> messageHandlerMap = divideMessageHandlerByGroup();
        for (String group : messageHandlerMap.keySet()) {
            BoosterConsumer.Builder boosterConsumerBuilder = new BoosterConsumer.Builder(group);
            boosterConsumerBuilder.setNameServerAddr(configurationProperties.getNameServerAddr());
            boosterConsumerBuilder.setConsumeThreadMin(consumerConfigurationProperties.getConsumeThreadMin());
            boosterConsumerBuilder.setConsumeThreadMax(consumerConfigurationProperties.getConsumeThreadMax());
            boosterConsumerBuilder.setConsumeFromWhere(consumerConfigurationProperties.getConsumeFromWhere());
            boosterConsumerBuilder.setMessageModel(consumerConfigurationProperties.getMessageModel());
            boosterConsumerBuilder.setSubscriptions(classifySubscription(messageHandlerMap.get(group)));
            BoosterConsumer boosterConsumer = boosterConsumerBuilder.build();
            boosterConsumer.listen();
        }
    }

    /**
     * 把消费者Group分组
     *
     * @return Map<String, List<MessageHandler>>
     */
    private Map<String, List<MessageHandler>> divideMessageHandlerByGroup() {
        Map<String, List<MessageHandler>> messageHandlerMap = new LinkedHashMap<>();
        if (!Objects.isNull(messageHandlerList) && !CollectionUtils.isEmpty(messageHandlerList)) {
            // TODO: 2020/09/25 消费者分组
        }
        return messageHandlerMap;
    }

    /**
     * 将同组的消费者订阅信息进行归类
     *
     * @param messageHandlerList 消费者列表
     * @return 订阅信息
     */
    private Subscription[] classifySubscription(List<MessageHandler> messageHandlerList) {
        // TODO: 2020/09/25 订阅信息分类
        return null;
    }

    @Autowired(required = false)
    public void setMessageHandlerList(List<MessageHandler> messageHandlerList) {
        this.messageHandlerList = messageHandlerList;
    }
}

package com.github.booster.autoconfigure;

import com.github.booster.annotation.Consumer;
import com.github.booster.common.util.CollectionUtils;
import com.github.booster.core.consumer.BoosterConsumer;
import com.github.booster.core.consumer.MessageHandler;
import com.github.booster.core.consumer.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.*;

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

    private Logger logger = LoggerFactory.getLogger(BoosterConsumerAutoConfiguration.class);

    private BoosterConfigurationProperties configurationProperties;

    private BoosterConsumerConfigurationProperties consumerConfigurationProperties;

    private List<MessageHandler> messageHandlerList;

    public BoosterConsumerAutoConfiguration(BoosterConfigurationProperties configurationProperties,
                                            BoosterConsumerConfigurationProperties consumerConfigurationProperties) {
        this.configurationProperties = configurationProperties;
        this.consumerConfigurationProperties = consumerConfigurationProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, List<MessageHandler>> messageHandlerMap = divideMessageHandlerByGroup();
        for (String group : messageHandlerMap.keySet()) {
            List<MessageHandler> currentGroupMessageHandlers = messageHandlerMap.get(group);
            BoosterConsumer.Builder boosterConsumerBuilder = new BoosterConsumer.Builder(group);
            boosterConsumerBuilder.setNameServerAddr(configurationProperties.getNameServerAddr());
            boosterConsumerBuilder.setConsumeThreadMin(consumerConfigurationProperties.getConsumeThreadMin());
            boosterConsumerBuilder.setConsumeThreadMax(consumerConfigurationProperties.getConsumeThreadMax());
            boosterConsumerBuilder.setConsumeFromWhere(consumerConfigurationProperties.getConsumeFromWhere());
            boosterConsumerBuilder.setMessageModel(consumerConfigurationProperties.getMessageModel());
            boosterConsumerBuilder.setSubscriptions(classifySubscription(currentGroupMessageHandlers));
            boosterConsumerBuilder.registerMessageHandlers(currentGroupMessageHandlers.toArray(new MessageHandler[0]));
            BoosterConsumer boosterConsumer = boosterConsumerBuilder.build();
            boosterConsumer.listen();
        }
    }

    /**
     * 把消费者Group分组
     *
     * @return Map<String, List < MessageHandler>>
     */
    private Map<String, List<MessageHandler>> divideMessageHandlerByGroup() {
        Map<String, List<MessageHandler>> messageHandlerMap = new LinkedHashMap<>();
        if (!CollectionUtils.isEmpty(messageHandlerList)) {
            for (MessageHandler messageHandler : messageHandlerList) {
                Class<? extends MessageHandler> messageHandlerClass = messageHandler.getClass();
                Consumer annotation = messageHandlerClass.getAnnotation(Consumer.class);
                String group = annotation.group();
                if (messageHandlerMap.containsKey(group)) {
                    messageHandlerMap.get(group).add(messageHandler);
                } else {
                    messageHandlerMap.put(group, CollectionUtils.list(true, messageHandler));
                }
            }
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
        List<Subscription> subscriptionList = new LinkedList<>();
        Map<String, List<String>> topicTagMap = new LinkedHashMap<>();

        for (MessageHandler messageHandler : messageHandlerList) {
            Class<? extends MessageHandler> messageHandlerClass = messageHandler.getClass();
            Consumer annotation = messageHandlerClass.getAnnotation(Consumer.class);
            String topic = annotation.topic();
            List<String> tagList = new ArrayList<>(Arrays.asList(annotation.tags()));
            if (topicTagMap.containsKey(topic)) {
                topicTagMap.get(topic).addAll(tagList);
            } else {
                topicTagMap.put(topic, tagList);
            }
        }

        for (String topic : topicTagMap.keySet()) {
            Subscription subscription = new Subscription();
            subscription.setTopic(topic);
            subscription.setTags(topicTagMap.get(topic).toArray(new String[0]));
            subscriptionList.add(subscription);
        }

        return subscriptionList.toArray(new Subscription[0]);
    }

    @Autowired(required = false)
    public void setMessageHandlerList(List<MessageHandler> messageHandlerList) {
        this.messageHandlerList = messageHandlerList;
    }
}

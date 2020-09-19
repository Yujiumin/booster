package com.github.booster.core.consumer;

import com.github.booster.annotation.Consumer;
import com.github.booster.common.util.CollectionUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 消息分发器
 *
 * @author Yujiumin
 * @version 2020/09/13
 */
public class MessageDispatcher implements MessageListenerConcurrently {

    private Logger logger = LoggerFactory.getLogger(MessageDispatcher.class);

    private Map<String, List<MessageHandler>> messageHandlerGroupByTopicMap;

    private Map<String, MessageTopicHandler> topicMessageHandlerMap;

    private MessageDispatcher() {
        messageHandlerGroupByTopicMap = new LinkedHashMap<>();
        topicMessageHandlerMap = new LinkedHashMap<>();
    }

    MessageDispatcher(MessageHandler[] messageHandlers) {
        this();
        if (!CollectionUtils.isEmpty(Arrays.asList(messageHandlers))) {
            // 按topic对MessageHandler进行分组
            for (MessageHandler messageHandler : messageHandlers) {
                Class<? extends MessageHandler> messageHandlerClass = messageHandler.getClass();
                Consumer consumerAnnotation = messageHandlerClass.getAnnotation(Consumer.class);
                if (Objects.isNull(consumerAnnotation)) {
                    String className = messageHandlerClass.getName();
                    String annotationName = Consumer.class.getName();
                    logger.warn("[{}] 类下没有用 [{}] 注解修饰", className, annotationName);
                } else {
                    String topic = consumerAnnotation.topic();
                    if (messageHandlerGroupByTopicMap.containsKey(topic)) {
                        List<MessageHandler> messageHandlerList = messageHandlerGroupByTopicMap.get(topic);
                        messageHandlerList.add(messageHandler);
                    } else {
                        List<MessageHandler> messageHandlerList = CollectionUtils.list(true, messageHandler);
                        messageHandlerGroupByTopicMap.put(topic, messageHandlerList);
                    }
                }
            }

            // 按topic配置处理器
            for (String topic : messageHandlerGroupByTopicMap.keySet()) {
                List<MessageHandler> messageHandlerList = messageHandlerGroupByTopicMap.get(topic);
                topicMessageHandlerMap.put(topic, new MessageTopicHandler(topic, messageHandlerList));
            }
        }
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messageExtList, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        logger.info("Message Size:{}", messageExtList.size());
        int delayLevelWhenNextConsume = consumeConcurrentlyContext.getDelayLevelWhenNextConsume();
        logger.info("DelayLevelWhenNextConsume:{}", delayLevelWhenNextConsume);
        int ackIndex = consumeConcurrentlyContext.getAckIndex();
        logger.info("AckIndex:{}", ackIndex);

        MessageExt messageExt = messageExtList.get(0);
        String topic = messageExt.getTopic();
        MessageTopicHandler messageTopicHandler = topicMessageHandlerMap.get(topic);
        return messageTopicHandler.consumeMessage(messageExt);
    }

}

package com.github.booster.core.consumer;

import com.github.booster.annotation.Consumer;
import com.github.booster.common.constant.BoosterConstant;
import com.github.booster.common.exception.BoosterException;
import com.github.booster.common.util.CollectionUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 主题消息处理器
 *
 * @author Yujiumin
 * @version 2020/09/14
 */
public class MessageTopicHandler implements MessageHandler {

    private Logger logger = LoggerFactory.getLogger(MessageTopicHandler.class);

    private Map<List<String>, MessageHandler> messageHandlerMap;

    private MessageHandler messageHandler;

    private MessageTopicHandler() {
        messageHandlerMap = new LinkedHashMap<>();
    }

    MessageTopicHandler(String topic, List<MessageHandler> messageHandlerList) {
        this();
        // 按Tag进行分类
        for (MessageHandler messageHandler : messageHandlerList) {
            Class<? extends MessageHandler> messageHandlerClass = messageHandler.getClass();
            Consumer consumerAnnotation = messageHandlerClass.getAnnotation(Consumer.class);
            List<String> tagList = Arrays.asList(consumerAnnotation.tags());

            // 检查当前的TAG与同TOPIC下的messageHandlers是否有重复消费的地方
            for (List<String> tagKey : messageHandlerMap.keySet()) {
                Collection<String> intersect = CollectionUtils.intersect(tagKey, tagList);
                boolean isTagRepeated = !CollectionUtils.isEmpty(intersect);
                if (isTagRepeated) {
                    Class<? extends MessageHandler> messageHandlerExistedClass = messageHandlerMap.get(tagKey).getClass();
                    logger.error("[{}] 与 [{}] 消费TAG重复", messageHandlerClass.getName(), messageHandlerExistedClass.getName());
                    logger.error("[{}] 的消费TAG为: {}", messageHandlerClass.getName(), tagList);
                    logger.error("[{}] 的消费TAG为: {}", messageHandlerExistedClass.getName(), tagKey);
                    logger.error("重复消费的TAG为: {}", intersect);
                    throw new BoosterException("消费重复");
                }
            }

            // 当前messageHandler是否消费任何TAG
            boolean isConsumeAnyTag = Objects.equals(tagList.size(), 1) && tagList.contains(BoosterConstant.TAG_ANY);
            if (isConsumeAnyTag) {
                this.messageHandler = messageHandler;
            } else {
                messageHandlerMap.put(tagList, messageHandler);
            }
        }

        if (!Objects.isNull(messageHandler)) {
            logger.info("[{}] 主题下的所有消息由 [{}] 处理", topic, messageHandler.getClass().getName());
            // 移除所有的messageHandler
            for (List<String> tagKey : messageHandlerMap.keySet()) {
                MessageHandler messageHandler = messageHandlerMap.get(tagKey);
                logger.warn("[{}] 不再具有消费能力", messageHandler.getClass().getName());
            }
            messageHandlerMap = null;
            System.gc();
        } else {
            for (List<String> tagKey : messageHandlerMap.keySet()) {
                MessageHandler messageHandler = messageHandlerMap.get(tagKey);
                String messageHandlerClassName = messageHandler.getClass().getName();
                logger.info("[{}] 主题下的 [{}] 标签下的消息由 {} 处理", topic, tagKey, messageHandlerClassName);
            }
        }
    }


    @Override
    public ConsumeConcurrentlyStatus consumeMessage(MessageExt messageExt) {
        String currentMessageTags = messageExt.getTags();
        logger.info("[{}]收到消息，TAG为[{}]", getClass().getName(), currentMessageTags);
        if (!Objects.isNull(messageHandler)) {
            // 当前有处理所有TAG的消息处理器
            return messageHandler.consumeMessage(messageExt);
        } else if (!Objects.isNull(currentMessageTags) && !Objects.equals(currentMessageTags, BoosterConstant.TAG_ANY)) {
            // 消息的标签不为空 && 并且消息的标签不为 “*”
            List<String> currentMessageTagList = Arrays.asList(currentMessageTags.split(BoosterConstant.TAG_SEPARATOR));
            for (List<String> tagList : messageHandlerMap.keySet()) {
                Collection<String> intersect = CollectionUtils.intersect(tagList, currentMessageTagList);
                if (!CollectionUtils.isEmpty(intersect)) {
                    // 和当前MessageHandler处理的TAG有交集
                    MessageHandler messageHandler = messageHandlerMap.get(tagList);
                    return messageHandler.consumeMessage(messageExt);
                }
            }
        }
        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
    }
}

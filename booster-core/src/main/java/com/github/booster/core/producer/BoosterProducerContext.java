package com.github.booster.core.producer;

import org.apache.rocketmq.client.producer.MQProducer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 生产者上下文
 *
 * @author Yujiumin
 * @version 2020/09/20
 */
public class BoosterProducerContext {

    private static final Map<String, MQProducer> PRODUCER_MAP = new ConcurrentHashMap<>(16);

    public static void registerProducer(String groupName, MQProducer mqProducer) {
        PRODUCER_MAP.put(groupName, mqProducer);
    }

    public static MQProducer getProducer(String groupName) {
        return PRODUCER_MAP.get(groupName);
    }

}

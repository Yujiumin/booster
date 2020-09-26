package com.github.booster.autoconfigure;

import com.github.booster.annotation.Producer;
import com.github.booster.common.util.CollectionUtils;
import com.github.booster.core.producer.AbstractMessageProducer;
import com.github.booster.core.producer.BoosterProducer;
import com.github.booster.core.producer.aspect.ProducerAspect;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 生产者自动配置
 *
 * @author Yujiumin
 * @version 2020/09/25
 */
@Configuration
@EnableAspectJAutoProxy
@ConditionalOnProperty(prefix = "booster.producer", name = "enabled", havingValue = "true")
@ComponentScan("com.github.booster.autoconfigure")
public class BoosterProducerAutoConfiguration implements InitializingBean {

    private BoosterConfigurationProperties configurationProperties;

    private BoosterProducerConfigurationProperties producerConfigurationProperties;

    private List<AbstractMessageProducer> producerList;

    public BoosterProducerAutoConfiguration(BoosterConfigurationProperties configurationProperties, BoosterProducerConfigurationProperties producerConfigurationProperties) {
        this.configurationProperties = configurationProperties;
        this.producerConfigurationProperties = producerConfigurationProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, List<AbstractMessageProducer>> messageProducerMap = divideProducerByGroup();
        for (String group : messageProducerMap.keySet()) {
            BoosterProducer.Builder boosterProducerBuilder = new BoosterProducer.Builder(group);
            boosterProducerBuilder.setNameServerAddr(configurationProperties.getNameServerAddr());
            boosterProducerBuilder.setRetryTimesWhenSendAsyncFailed(producerConfigurationProperties.getRetryTimesWhenSendAsyncFailed());
            boosterProducerBuilder.setVipChannelEnabled(producerConfigurationProperties.getVipChannelEnabled());
            BoosterProducer boosterProducer = boosterProducerBuilder.build();
            boosterProducer.start();
        }
    }

    private Map<String, List<AbstractMessageProducer>> divideProducerByGroup() {
        Map<String, List<AbstractMessageProducer>> messageProducerMap = new LinkedHashMap<>();
        for (AbstractMessageProducer producer : producerList) {
            Class<? extends AbstractMessageProducer> producerClass = producer.getClass();
            Producer annotation = producerClass.getAnnotation(Producer.class);
            String group = annotation.group();
            if (messageProducerMap.containsKey(group)) {
                messageProducerMap.get(group).add(producer);
            } else {
                messageProducerMap.put(group, CollectionUtils.list(true, producer));
            }
        }
        return messageProducerMap;
    }

    @Autowired(required = false)
    public void setProducerList(List<AbstractMessageProducer> producerList) {
        this.producerList = producerList;
    }

    @Bean
    public ProducerAspect producerAspect() {
        return new ProducerAspect();
    }
}

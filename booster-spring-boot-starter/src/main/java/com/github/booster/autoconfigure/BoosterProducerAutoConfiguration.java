package com.github.booster.autoconfigure;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 生产者自动配置
 *
 * @author Yujiumin
 * @version 2020/09/25
 */
@Configuration
@ConditionalOnProperty(prefix = "booster.producer", name = "enabled", havingValue = "true")
@ComponentScan("com.github.booster.autoconfigure")
public class BoosterProducerAutoConfiguration implements InitializingBean {

    private BoosterConfigurationProperties configurationProperties;

    private BoosterProducerConfigurationProperties producerConfigurationProperties;

    public BoosterProducerAutoConfiguration(BoosterConfigurationProperties configurationProperties, BoosterProducerConfigurationProperties producerConfigurationProperties) {
        this.configurationProperties = configurationProperties;
        this.producerConfigurationProperties = producerConfigurationProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

}

package com.github.booster.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 生产者配置
 *
 * @author Yujiumin
 * @version 2020/09/25
 */
@Configuration
@ConfigurationProperties("booster.producer")
public class BoosterProducerConfigurationProperties {

    private Boolean enabled = false;

    private Integer retryTimesWhenSendAsyncFailed = 3;

    private Boolean vipChannelEnabled = false;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getRetryTimesWhenSendAsyncFailed() {
        return retryTimesWhenSendAsyncFailed;
    }

    public void setRetryTimesWhenSendAsyncFailed(Integer retryTimesWhenSendAsyncFailed) {
        this.retryTimesWhenSendAsyncFailed = retryTimesWhenSendAsyncFailed;
    }

    public Boolean getVipChannelEnabled() {
        return vipChannelEnabled;
    }

    public void setVipChannelEnabled(Boolean vipChannelEnabled) {
        this.vipChannelEnabled = vipChannelEnabled;
    }
}

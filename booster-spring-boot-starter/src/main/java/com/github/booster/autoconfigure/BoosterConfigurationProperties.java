package com.github.booster.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 全局配置
 *
 * @author Yujiumin
 * @version 2020/09/25
 */
@Configuration
@ConfigurationProperties("booster")
public class BoosterConfigurationProperties {

    private String nameServerAddr = "127.0.0.1:9876";

    public String getNameServerAddr() {
        return nameServerAddr;
    }

    public void setNameServerAddr(String nameServerAddr) {
        this.nameServerAddr = nameServerAddr;
    }
}

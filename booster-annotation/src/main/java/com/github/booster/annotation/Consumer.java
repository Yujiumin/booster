package com.github.booster.annotation;

import java.lang.annotation.*;

/**
 * RocketMQ 消费者注解
 *
 * @author Yujiumin
 * @version 2020/9/7
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Consumer {

    /**
     * @return 组名称
     */
    String group() default "DEFAULT_GROUP";

    /**
     * @return 订阅主题
     */
    String topic();

    /**
     * @return 消息标签
     */
    String[] tags() default "*";

}

package com.github.booster.annotation;

import java.lang.annotation.*;

/**
 * 消息注解
 *
 * @author Yujiumin
 * @version 2020/09/22
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Message {

    /**
     * @return 消息主题
     */
    String topic();

    /**
     * @return 消息标签
     */
    String[] tags() default "*";

    /**
     * @return 消息关键字
     */
    String[] keys() default "";


}

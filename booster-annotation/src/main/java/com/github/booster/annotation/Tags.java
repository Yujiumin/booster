package com.github.booster.annotation;

import java.lang.annotation.*;

/**
 * 消息标签注解
 *
 * @author Yujiumin
 * @version 2020/9/10
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Tags {

    /**
     * @return 标签
     */
    String[] value();

}

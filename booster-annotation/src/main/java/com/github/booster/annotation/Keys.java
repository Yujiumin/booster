package com.github.booster.annotation;

import java.lang.annotation.*;

/**
 * 消息关键字
 *
 * @author Yujiumin
 * @version 2020/9/10
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Keys {

    /**
     * @return 关键字
     */
    String[] value();
}

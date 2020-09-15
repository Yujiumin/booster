package com.github.booster.annotation;

import java.lang.annotation.*;

/**
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

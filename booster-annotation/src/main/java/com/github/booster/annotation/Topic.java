package com.github.booster.annotation;

import java.lang.annotation.*;

/**
 * @author Yujiumin
 * @version 2020/9/10
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Topic {

    /**
     * @return 主题
     */
    String value();

}

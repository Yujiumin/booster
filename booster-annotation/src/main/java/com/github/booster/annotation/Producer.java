package com.github.booster.annotation;

import java.lang.annotation.*;

/**
 * @author Yujiumin
 * @version 2020/9/10
 */
@Documented
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Producer {

    /**
     * @return 所在组
     */
    String group() default "DEFAULT_GROUP";

}

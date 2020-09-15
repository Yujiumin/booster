package com.github.booster.core.builder;

/**
 * @author Yujiumin
 * @version 2020/09/13
 */
public abstract class AbstractBuilder<T> {

    /**
     * 构建对象
     *
     * @return 构建的对象
     */
   public abstract T build();
}

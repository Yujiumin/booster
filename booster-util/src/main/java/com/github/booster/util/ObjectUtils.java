package com.github.booster.util;

import java.util.Objects;

/**
 * @author Yujiumin
 * @version 2020/9/15
 */
public class ObjectUtils {

    /**
     * 判断对象是否为空,如果不为空则返回原对象,为空则返回默认值
     *
     * @param obj          被判断对象
     * @param defaultValue 默认值
     * @param <T>          被判断对象泛型
     * @return 最终值
     */
    public static <T> T isNull(T obj, T defaultValue) {
        return Objects.isNull(obj) ? defaultValue : obj;
    }

}

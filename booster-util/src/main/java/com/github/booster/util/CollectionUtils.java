package com.github.booster.util;

import java.util.*;

/**
 * 集合工具类
 *
 * @author Yujiumin
 * @version 2020/9/7
 */
public class CollectionUtils {

    /**
     * @param isLinked
     * @param element
     * @param <T>
     * @return
     */
    public static <T> List<T> list(boolean isLinked, T element) {
        List<T> list = isLinked ? new LinkedList<>() : new ArrayList<>();
        list.add(element);
        return list;
    }

    /**
     * @param isLinked
     * @param key
     * @param value
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Map<K, V> map(boolean isLinked, K key, V value) {
        Map<K, V> map = isLinked ? new LinkedHashMap<>(16) : new HashMap<>(16);
        map.put(key, value);
        return map;
    }

    /**
     * 取两个集合的交集
     *
     * @param collection1
     * @param collection2
     * @param <T>
     * @return
     */
    public static <T> Collection<T> intersect(Collection<T> collection1, Collection<T> collection2) {
        List<T> intersection = new LinkedList<>();
        for (T element : collection1) {
            if (collection2.contains(element)) {
                intersection.add(element);
            }
        }
        return intersection;
    }

    /**
     * 判断集合是否为空
     *
     * @param collection
     * @return
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 判断Map是否为空
     *
     * @param map
     * @return
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

}

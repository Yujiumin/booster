package com.github.booster.common.util;

/**
 * @author Yujiumin
 * @version 2020/9/15
 */
public class StringUtils {

    /**
     * 将字符串连接起来
     *
     * @param delimiter 分隔符
     * @param strings   字符串数组
     * @return 拼接好的字符串
     */
    public static String link(CharSequence delimiter, String... strings) {
        return String.join(ObjectUtils.nullOrElse(delimiter, ""), strings);
    }


}

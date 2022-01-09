package org.sheedon.rr.timeout;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * ResourceBundle 工具类
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/9 9:22 下午
 */
public class ResourceBundleUtils {

    /**
     * 获取资源文字
     */
    public static String getResourceString(String baseName, String key) {
        Locale locale = Locale.getDefault();
        ResourceBundle my = ResourceBundle.getBundle(baseName, locale);
        return my.getString(key);
    }
}

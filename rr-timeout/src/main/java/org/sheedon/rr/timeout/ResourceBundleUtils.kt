package org.sheedon.rr.timeout

import java.util.*

/**
 * ResourceBundle 工具类
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/9 9:22 下午
 */
object ResourceBundleUtils {
    /**
     * 获取资源文字
     */
    @JvmStatic
    fun getResourceString(baseName: String, key: String): String {
        val locale = Locale.getDefault()
        val my = ResourceBundle.getBundle(baseName, locale)
        return my.getString(key)
    }
}
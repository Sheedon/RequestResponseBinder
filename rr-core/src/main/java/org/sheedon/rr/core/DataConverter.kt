package org.sheedon.rr.core

/**
 * 内容转换器
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 4:55 下午
 */
interface DataConverter<F, T> {
    fun convert(value: F): T
}
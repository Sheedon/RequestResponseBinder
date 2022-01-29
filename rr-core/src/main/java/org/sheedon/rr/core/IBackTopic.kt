package org.sheedon.rr.core

/**
 * 反馈主题信息
 * 请求数据和响应结构中都应存在¬
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/22 11:32 下午
 */
interface IBackTopic<T> {
    /**
     * 反馈绑定主题
     */
    fun backTopic(): T
}
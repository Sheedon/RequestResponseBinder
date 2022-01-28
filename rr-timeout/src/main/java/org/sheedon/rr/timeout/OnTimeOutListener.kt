package org.sheedon.rr.timeout

import java.util.concurrent.TimeoutException

/**
 * 超时监听者，反馈超时信息的ID
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 6:11 下午
 */
interface OnTimeOutListener<T> {
    /**
     * 超时消息
     *
     * @param id 超时消息ID
     */
    fun onTimeOut(id: T, e: TimeoutException?)
}
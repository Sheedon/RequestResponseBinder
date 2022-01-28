package org.sheedon.rr.timeout

/**
 * 延迟事件
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 4:59 下午
 */
class DelayEvent<T> {
    var id: T? = null
        private set
    var timeOut: Long = 0
        private set

    companion object {
        @JvmStatic
        fun <T> build(id: T, timeOut: Long): DelayEvent<T> {
            val event = DelayEvent<T>()
            event.id = id
            event.timeOut = timeOut
            return event
        }
    }
}
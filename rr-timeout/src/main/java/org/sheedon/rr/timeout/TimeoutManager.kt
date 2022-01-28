package org.sheedon.rr.timeout

/**
 * 超时管理者职责，
 * 新增事件，通过ID移除事件，销毁。
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 6:08 下午
 */
abstract class TimeoutManager<T> {

    @JvmField
    protected var listener: OnTimeOutListener<T>? = null
    @JvmName("setTimeOutListener")
    fun setListener(listener:OnTimeOutListener<T>){
        this.listener = listener
    }

    /**
     * 新增超时事件
     *
     * @param event 超时事件
     */
    abstract fun addEvent(event: DelayEvent<T>)

    /**
     * 移除超时事件
     *
     * @param id 事件ID
     */
    abstract fun removeEvent(id: T)

    /**
     * 销毁
     */
    abstract fun destroy()
}
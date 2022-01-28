package org.sheedon.rr.timeout.android

import android.os.Handler
import org.sheedon.rr.timeout.ResourceBundleUtils.getResourceString
import kotlin.jvm.JvmOverloads
import org.sheedon.rr.timeout.TimeoutManager
import android.os.HandlerThread
import android.os.Message
import org.sheedon.rr.timeout.DelayEvent
import java.util.concurrent.TimeoutException

/**
 * 超时执行者，处理超时事件发送
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 6:33 下午
 */
class TimeOutHandler<T> @JvmOverloads constructor(name: String? = TimeOutHandler::class.java.canonicalName) :
    TimeoutManager<T>() {
    // 处理线程
    private var triggerThread: HandlerThread

    // 事务处理器
    private val workHandler: Handler

    companion object {
        private var TIMEOUT: String? = null
        private const val BASENAME = "timeout"
        private const val RESOURCE_KEY = "data_time_out"
    }

    init {
        TIMEOUT = getResourceString(BASENAME, RESOURCE_KEY)

        // 创建一个HandlerThread 用于执行消息Loop
        triggerThread = HandlerThread(name)
        triggerThread.start()

        // 创建绑定在triggerThread的handler
        workHandler = Handler(triggerThread.looper) { msg: Message ->
            if (listener != null) {
                @Suppress("UNCHECKED_CAST")
                listener!!.onTimeOut(
                    msg.obj as T,
                    TimeoutException(TIMEOUT)
                )
            }
            false
        }
    }

    /**
     * 新增超时事件
     *
     * @param event 超时事件
     */
    override fun addEvent(event: DelayEvent<T>) {
        val obtain = Message.obtain()
        obtain.obj = event.id
        workHandler.sendMessageDelayed(obtain, event.timeOut - System.currentTimeMillis())
    }

    /**
     * 移除超时事件
     *
     * @param id 超时事件Id
     */
    override fun removeEvent(id: T) {
        workHandler.removeCallbacksAndMessages(id)
    }

    /**
     * 销毁
     */
    override fun destroy() {
        workHandler.removeCallbacksAndMessages(null)
        triggerThread.quitSafely()
    }
}
package org.sheedon.rr.dispatcher

import org.sheedon.rr.core.EventBehavior
import java.util.concurrent.Executors

/**
 * 默认事件行为服务,将事件放入线程池去执行
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/9 10:41 上午
 */
class DefaultEventBehaviorService : EventBehavior {
    // 单线程池处理任务提交
    private val publishService = Executors.newSingleThreadExecutor()

    // 反馈数据，可能是高并发，使用缓存线程池
    private val callbackService = Executors.newCachedThreadPool()

    /**
     * 将反馈事件放入 singleThreadExecutor 中去串行执行
     *
     * @param requestRunnable Runnable 请求任务
     * @return 返回true，代表当前以被处理，无需其他事件执行者再去操作
     */
    override fun enqueueRequestEvent(requestRunnable: Runnable): Boolean {
        publishService.execute(requestRunnable)
        return true
    }

    /**
     * 将反馈事件放入 cachedThreadPool 中去并发执行
     *
     * @param callbackRunnable Runnable 反馈任务
     * @return 返回true，代表当前以被处理，无需其他事件执行者再去操作
     */
    override fun enqueueCallbackEvent(callbackRunnable: Runnable): Boolean {
        callbackService.execute(callbackRunnable)
        return true
    }
}
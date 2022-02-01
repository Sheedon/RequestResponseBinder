package org.sheedon.rr.dispatcher

import org.sheedon.rr.core.*
import org.sheedon.rr.timeout.DelayEvent.Companion.build
import org.sheedon.rr.core.ReadyTask.Companion.build
import org.sheedon.rr.timeout.DelayEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * 默认的事件处理池，用于管理未完成的请求响应记录
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/9 2:45 下午
 */
class DefaultEventManager<BackTopic, RequestData, ResponseData> :
    EventManager<BackTopic, String, RequestData, ResponseData> {
    // 以请求ID为键，以请求任务为值的请求数据池
    private val readyPool: MutableMap<String, ReadyTask<BackTopic, String, RequestData, ResponseData>> =
        ConcurrentHashMap()

    // 主题队列池，反馈主题为键，同样的反馈主题的内容，依次存入有序队列中
    private val topicDequePool: MutableMap<BackTopic, Deque<String>> = LinkedHashMap()

    // 监听反馈池
    private val callbackPool: MutableMap<BackTopic, ReadyTask<BackTopic, String, RequestData, ResponseData>?> =
        ConcurrentHashMap()

    override fun push(
        request: IRequest<BackTopic, RequestData>,
        defaultDelayMilliSecond: Long,
        callback: Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>>?
    ): DelayEvent<String> {
        val id = UUID.randomUUID().toString()
        val delayMilliSecond: Long =
            if (request.delayMilliSecond() < 0) defaultDelayMilliSecond else request.delayMilliSecond()
        val event = build(id, System.currentTimeMillis() + delayMilliSecond)
        // 添加准备反馈任务集合
        readyPool[id] = build(id, request, callback)
        // 添加网络反馈集合
        topicDequePool[request.backTopic()] = getNetCallDeque(request.backTopic(), id)
        return event
    }

    /**
     * 填充反馈集合
     *
     * @param backTopic 反馈主题
     * @param id        UUID
     * @return Deque<String>
    </String> */
    private fun getNetCallDeque(backTopic: BackTopic, id: String): Deque<String> {
        var callbacks = topicDequePool[backTopic]
        if (callbacks == null) {
            callbacks = ArrayDeque()
        }
        callbacks.add(id)
        return callbacks
    }

    /**
     * 根据反馈主题获取Callback
     *
     * @param topic 反馈主题
     * @return Callback
     */
    override fun popByTopic(topic: BackTopic): ReadyTask<BackTopic, String, RequestData, ResponseData>? {
        synchronized(topicDequePool) {
            val deque = topicDequePool[topic]
            if (deque == null || deque.size == 0) return null
            val id = deque.removeFirst()
            return popById(id)
        }
    }

    /**
     * 根据请求ID获取Callback
     *
     * @param id 请求ID
     * @return Callback
     */
    override fun popById(id: String): ReadyTask<BackTopic, String, RequestData, ResponseData>? {
        return readyPool.remove(id)
    }

    override fun subscribe(
        request: IRequest<BackTopic, RequestData>,
        callback: Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>>?
    ): Boolean {
        callbackPool[request.backTopic()] = build("", request, callback)
        return true
    }

    override fun unsubscribe(backTopic: BackTopic): Boolean {
        val remove = callbackPool.remove(backTopic)
        return remove != null
    }

    override fun loadObservable(topic: BackTopic): ReadyTask<BackTopic, String, RequestData, ResponseData>? {
        return callbackPool[topic]
    }

}
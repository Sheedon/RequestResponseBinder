package org.sheedon.rr.dispatcher

import org.sheedon.rr.core.*

import org.sheedon.rr.timeout.TimeoutManager
import org.sheedon.rr.core.DispatchAdapter.OnCallListener
import org.sheedon.rr.timeout.OnTimeOutListener
import java.util.concurrent.TimeoutException

/**
 * 请求响应调度者
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 9:36 下午
 */
class Dispatcher<BackTopic, ID, RequestData, ResponseData>(
    private val behaviorServices: List<EventBehavior>,// 事件行为服务，将任务放入服务中去执行
    private val eventManagerPool: List<EventManager<BackTopic, ID, RequestData, ResponseData>>,// 事件管理者-事件池
    private val timeoutManager: TimeoutManager<ID>,// 请求超时管理者
    dispatchAdapter: DispatchAdapter<RequestData, ResponseData>,// 调度适配器（请求适配器+反馈监听器）
    private val backTopicConverters: MutableList<DataConverter<ResponseData, BackTopic>>,// 反馈主题转换器
    private val responseAdapter: ResponseAdapter<BackTopic, ResponseData>// 结果适配器
) : DispatchManager<BackTopic, RequestData, ResponseData>, OnCallListener<ResponseData> {

    // 请求适配器
    private val requestAdapter: RequestAdapter<RequestData> = dispatchAdapter.loadRequestAdapter()

    init {
        dispatchAdapter.bindCallListener(this)
        this.timeoutManager.setListener(TimeOutListener())
    }

    override fun requestAdapter(): RequestAdapter<RequestData> {
        return requestAdapter
    }

    override fun enqueueRequest(runnable: Runnable) {
        for (service in behaviorServices) {
            if (service.enqueueRequestEvent(runnable)) {
                return
            }
        }
    }

    override fun addBinder(
        request: IRequest<BackTopic, RequestData>,
        callback: Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>>?
    ) {
        for (manager in eventManagerPool) {
            val event = manager.push(request, callback)
            if (event != null) {
                timeoutManager.addEvent(event)
                return
            }
        }
    }

    override fun addObservable(
        request: IRequest<BackTopic, RequestData>,
        callback: Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>>?
    ) {
        for (manager in eventManagerPool) {
            val subscribed = manager.subscribe(request, callback)
            if (subscribed) {
                return
            }
        }
    }

    override fun removeObservable(backTopic: BackTopic) {
        for (manager in eventManagerPool) {
            val subscribed = manager.unsubscribe(backTopic)
            if (subscribed) {
                return
            }
        }
    }

    /**
     * 执行响应反馈操作
     *
     * @param message ResponseData
     */
    override fun callResponse(message: ResponseData) {
        enqueueResponse {
            backTopicConverters.forEach {
                val backTopic: BackTopic? = it.convert(message)
                backTopic?.run {
                    val response: IResponse<BackTopic, ResponseData> =
                        responseAdapter.buildResponse(this, message)
                    onResponse(response)
                    return@enqueueResponse
                }
            }
        }
    }

    /**
     * 反馈行为入队，按照预定策略去执行反馈动作
     *
     * @param runnable 反馈的Runnable
     */
    private fun enqueueResponse(runnable: Runnable) {
        for (service in behaviorServices) {
            if (service.enqueueCallbackEvent(runnable)) {
                return
            }
        }
    }

    override fun onResponse(response: IResponse<BackTopic, ResponseData>) {
        val backTopic = response.backTopic()
        var sign = 0B00
        for (manager in eventManagerPool) {
            val callTask = manager.loadObservable(backTopic)
            if (sign xor 1 != 0 && callTask != null) {
                sign = sign or 1
                callTask.callback?.onResponse(callTask.request!!, response)
            }

            val task = manager.popByTopic(backTopic)
            if (sign xor 2 != 0 && task != null) {
                sign = sign or 2
                timeoutManager.removeEvent(task.id!!)
                val request = task.request
                val callback: Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>>? =
                    task.callback
                callback?.onResponse(request!!, response)
            }

            if (sign xor 3 == 0) return
        }
    }

    /**
     * 超时监听器
     */
    private inner class TimeOutListener : OnTimeOutListener<ID> {
        override fun onTimeOut(id: ID, e: TimeoutException?) {
            for (manager in eventManagerPool) {
                val task = manager.popById(id)
                if (task != null) {
                    timeoutManager.removeEvent(task.id!!)
                    val request = task.request
                    val callback: Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>>? =
                        task.callback
                    val topic = task.request!!.backTopic()
                    val failureResponse: IResponse<BackTopic, ResponseData> =
                        responseAdapter.buildFailure(topic, e!!.message!!)
                    callback?.onResponse(request!!, failureResponse)
                    return
                }
            }
        }
    }
}
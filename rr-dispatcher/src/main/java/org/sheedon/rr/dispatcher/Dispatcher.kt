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
    private val timeout: Long,
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

    override fun responseAdapter(): ResponseAdapter<BackTopic, ResponseData> {
        return responseAdapter
    }

    override fun enqueueRequest(runnable: Runnable) {
        log.info("Dispatcher", "enqueueRequest runnable")
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
        log.info("Dispatcher", "addBinder request($request) and callback($callback)")
        for (manager in eventManagerPool) {
            val event = manager.push(request, timeout, callback)
            if (event != null) {
                log.info("Dispatcher", "addBinder to addEvent success")
                timeoutManager.addEvent(event)
                return
            }
        }
    }

    override fun addObservable(
        request: IRequest<BackTopic, RequestData>,
        callback: Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>>?
    ) {
        log.info("Dispatcher", "addObservable request($request) and callback($callback)")
        for (manager in eventManagerPool) {
            val subscribed = manager.subscribe(request, callback)
            if (subscribed) {
                log.info("Dispatcher", "addObservable to subscribe success")
                return
            }
        }
    }

    override fun removeObservable(backTopic: BackTopic) {
        log.info("Dispatcher", "removeObservable backTopic($backTopic)")
        for (manager in eventManagerPool) {
            val subscribed = manager.unsubscribe(backTopic)
            if (subscribed) {
                log.info("Dispatcher", "removeObservable unsubscribe success")
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
                log.info("Dispatcher", "callResponse backTopic($backTopic)")
                backTopic?.run {
                    val response: IResponse<BackTopic, ResponseData> =
                        responseAdapter.buildResponse(this, message)
                    log.info("Dispatcher", "callResponse response($response)")
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
        log.info("Dispatcher", "enqueueResponse runnable")
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
                log.info(
                    "Dispatcher",
                    "onResponse observable by backTopic($backTopic), response is $response"
                )
                sign = sign or 1
                callTask.callback?.onResponse(callTask.request!!, response)
            }

            val task = manager.popByTopic(backTopic)
            if (sign xor 2 != 0 && task != null) {
                log.info(
                    "Dispatcher",
                    "onResponse call by backTopic($backTopic), response is $response"
                )
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
                    log.info(
                        "Dispatcher",
                        "onTimeOut task($task)"
                    )
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
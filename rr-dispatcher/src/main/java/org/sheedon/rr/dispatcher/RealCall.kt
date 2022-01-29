package org.sheedon.rr.dispatcher

import org.sheedon.rr.core.*
import org.sheedon.rr.timeout.ResourceBundleUtils.getResourceString
import org.sheedon.rr.dispatcher.model.BaseResponse
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 真实Call
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/9 5:09 下午
 */
class RealCall<BackTopic, ID, RequestData, ResponseData>(
    private val client: AbstractClient<BackTopic, ID, RequestData, ResponseData>,
    // 请求对象
    private val originalRequest: IRequest<BackTopic, RequestData>
) : Call<BackTopic, RequestData, ResponseData> {

    // 是否执行取消操作
    @Volatile
    private var canceled = false
    override var isCanceled: Boolean = canceled

    // 是否执行完成
    private val executed = AtomicBoolean()

    override val isExecuted: Boolean
        get() = executed.get()

    companion object {
        private const val BASENAME = "dispatcher"
        private const val DISPATCHER_KEY = "request_error"
    }

    override fun <RRCallback : Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>>?> enqueue(
        callback: RRCallback
    ) {
        check(executed.compareAndSet(false, true)) { "Already Executed" }

        val manager = client.dispatchManager
        manager.enqueueRequest(AsyncCall(client, originalRequest, callback))
    }

    override fun publish() {
        this.enqueue<Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>>?>(
            null
        )
    }

    override fun cancel() {
        isCanceled = true
    }

    internal inner class AsyncCall(
        private val client: AbstractClient<BackTopic, ID, RequestData, ResponseData>,
        private val originalRequest: IRequest<BackTopic, RequestData>,
        private val responseCallback: Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>>?
    ) : NamedRunnable("AsyncCall %s", originalRequest) {
        override fun execute() {
            if (isCanceled) {
                return
            }
            val isNeedCallback = responseCallback != null
            val manager = client.dispatchManager
            val adapter = manager.requestAdapter()
            var body = originalRequest.body()
            body = adapter!!.checkRequestData(body)
            if (isNeedCallback) {
                manager.addBinder(originalRequest, responseCallback)
            }
            val isSuccess = adapter.publish(body)
            if (!isSuccess) {
                val response: IResponse<BackTopic, ResponseData> = BaseResponse.build(
                    originalRequest.backTopic(),
                    getResourceString(BASENAME, DISPATCHER_KEY)
                )
                manager.onResponse(response)
            }
        }
    }
}
package org.sheedon.rr.dispatcher

import org.sheedon.rr.core.*

/**
 * 真实可观察者
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/14 11:04 下午
 */
class RealObservable<BackTopic, ID, RequestData, ResponseData>(
    private val client: AbstractClient<BackTopic, ID, RequestData, ResponseData>,
    // 请求对象
    private val originalRequest: IRequest<BackTopic, RequestData>
) : Observable<BackTopic, RequestData, ResponseData> {

    // 是否执行取消操作
    override var isCanceled = false
        private set

    override fun <RRCallback : Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>>> subscribe(
        callback: RRCallback
    ) {
        val manager = client.dispatchManager
        manager.addObservable(originalRequest, callback)
    }

    override fun cancel() {
        isCanceled = true
    }
}
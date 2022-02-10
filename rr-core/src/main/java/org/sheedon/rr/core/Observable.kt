package org.sheedon.rr.core

/**
 * 订阅的职责，包括「订阅」，取消订阅
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 4:29 下午
 */
interface Observable<BackTopic, RequestData, ResponseData> {
    /**
     * 订阅，指代于需要将请求和反馈绑定的任务
     *
     * @param callback 反馈内容
     */
    fun <RRCallback : Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>>> subscribe(
        callback: RRCallback
    )

    /**
     * 获取请求数据
     *
     * @return Request 请求信息
     */
    fun <Request : IRequest<BackTopic, RequestData>> request(): Request

    /**
     * 消息是否取消
     */
    fun isCanceled(): Boolean

    /**
     * 取消任务
     */
    fun cancel()
}
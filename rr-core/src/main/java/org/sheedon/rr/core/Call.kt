package org.sheedon.rr.core

/**
 * 调度的职责，包括「入队」，「提交」，「取消」操作
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 4:22 下午
 */
interface Call<BackTopic, RequestData, ResponseData> {
    /**
     * 请求入队，指代于需要将请求和反馈绑定的任务
     *
     * @param callback 反馈内容
     */
    fun <RRCallback : Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>>?> enqueue(
        callback: RRCallback
    )

    /**
     * 请求提交，标志着这个请求无需监听反馈
     */
    fun publish()

    /**
     * 消息是否取消
     */
    val isCanceled: Boolean

    /**
     * 取消任务
     */
    fun cancel()

    /**
     * 是否被执行完成
     */
    val isExecuted: Boolean
}
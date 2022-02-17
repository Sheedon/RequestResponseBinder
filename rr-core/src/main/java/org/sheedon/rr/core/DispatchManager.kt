package org.sheedon.rr.core

/**
 * 作为「请求响应模式」下，请求和反馈信息的输入。
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 3:01 下午
 */
interface DispatchManager<BackTopic, RequestData, ResponseData> {
    fun requestAdapter(): RequestAdapter<RequestData>?

    fun responseAdapter(): ResponseAdapter<BackTopic, ResponseData>

    /**
     * 将请求行为入队，按预定策略去执行请求动作
     *
     * @param runnable 处理事件
     */
    fun enqueueRequest(runnable: Runnable)

    /**
     * 添加一个请求响应关联者，用于绑定一个请求与一个响应
     *
     * @param request  请求数据
     * @param callback 反馈监听器
     */
    fun addBinder(
        request: IRequest<BackTopic, RequestData>,
        callback: Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>>?
    )

    /**
     * 添加一个可观察信息，用于订阅某个主题的反馈信息
     *
     * @param request  请求数据
     * @param callback 反馈监听器
     */
    fun addObservable(
        request: IRequest<BackTopic, RequestData>,
        callback: Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>>?
    )

    /**
     * 移除一个可观察信息，用于取消订阅某个主题的反馈信息的关联项
     *
     * @param request  请求数据
     * @param callback 反馈监听器
     */
    fun removeObservable(
        backTopic: BackTopic
    )

    /**
     * 反馈结果监听
     *
     * @param response 反馈结果
     */
    fun onResponse(response: IResponse<BackTopic, ResponseData>)
}
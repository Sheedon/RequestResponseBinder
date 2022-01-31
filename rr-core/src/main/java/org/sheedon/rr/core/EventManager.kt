package org.sheedon.rr.core

import org.sheedon.rr.timeout.DelayEvent

/**
 * 事件管理者
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 5:03 下午
 */
interface EventManager<BackTopic, ID, RequestData, ResponseData> {
    /**
     * 将反馈主题和反馈监听器添加到事件中，并且返回延迟事件，用于处理超时任务
     *
     * @param request  请求对象
     * @param callback 反馈监听器
     * @return DelayEvent<T>
    </T> */
    fun push(
        request: IRequest<BackTopic, RequestData>,
        callback: Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>>?
    ): DelayEvent<ID>?

    /**
     * 根据反馈主题获取反馈监听者
     *
     * @param topic 反馈主题
     * @return Callback
     */
    fun popByTopic(topic: BackTopic): ReadyTask<BackTopic, ID, RequestData, ResponseData>?

    /**
     * 根据请求ID获取反馈监听者
     *
     * @param id 请求ID
     * @return Callback
     */
    fun popById(id: ID): ReadyTask<BackTopic, ID, RequestData, ResponseData>?

    /**
     * 通过主题和反馈监听者，实现监听内容的绑定
     *
     * @param request  请求对象
     * @param callback 反馈监听者
     */
    fun subscribe(
        request: IRequest<BackTopic, RequestData>,
        callback: Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>>?
    ): Boolean

    /**
     * 根据主题，取消订阅的绑定
     *
     * @param backTopic  反馈主题
     */
    fun unsubscribe(backTopic: BackTopic): Boolean

    /**
     * 通过反馈绑定主题 加载 反馈监听者
     *
     * @param topic 反馈绑定主题
     * @return 反馈监听者
     */
    fun loadObservable(topic: BackTopic): ReadyTask<BackTopic, String, RequestData, ResponseData>?
}
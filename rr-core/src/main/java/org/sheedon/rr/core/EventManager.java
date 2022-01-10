package org.sheedon.rr.core;

import org.sheedon.rr.timeout.DelayEvent;

/**
 * 事件管理者
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 5:03 下午
 */
public interface EventManager<Topic, T> {

    /**
     * 将反馈主题和反馈监听器添加到事件中，并且返回延迟事件，用于处理超时任务
     *
     * @param request   请求对象
     * @param callback  反馈监听器
     * @param <Request> 请求对象类型
     * @return DelayEvent<T>
     */
    <Request extends BaseRequest<?, Topic>> DelayEvent<T> push(Request request,
                                                               Callback<Request, ?> callback);

    /**
     * 根据反馈主题获取反馈监听者
     *
     * @param topic 反馈主题
     * @return Callback<?, ?>
     */
    <Request extends BaseRequest<?, Topic>> ReadyTask<T, Request> popByTopic(Topic topic);

    /**
     * 根据请求ID获取反馈监听者
     *
     * @param id 请求ID
     * @return Callback<?, ?>
     */
    <Request extends BaseRequest<?, Topic>>
    ReadyTask<T, Request> popById(T id);

    /**
     * 通过主题和反馈监听者，实现监听内容的绑定
     *
     * @param topic    主题
     * @param callback 反馈监听者
     */
    boolean subscribe(Topic topic, Callback<?, ?> callback);

    /**
     * 通过反馈绑定主题 加载 反馈监听者
     *
     * @param topic 反馈绑定主题
     * @return 反馈监听者
     */
    Callback<?, ?> loadObservable(Topic topic);
}

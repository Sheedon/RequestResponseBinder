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
     * @param topic    反馈主题
     * @param callback 反馈监听器
     * @return DelayEvent<T>
     */
    DelayEvent<T> push(Topic topic, Callback<?, ?> callback);

    /**
     * 根据反馈主题获取反馈监听者
     *
     * @param topic 反馈主题
     * @return Callback<?, ?>
     */
    Callback<?, ?> popByTopic(Topic topic);

    /**
     * 根据请求ID获取反馈监听者
     *
     * @param id 请求ID
     * @return Callback<?, ?>
     */
    Callback<?, ?> popById(T id);

    /**
     * 通过主题和反馈监听者，实现监听内容的绑定
     *
     * @param topic    主题
     * @param callback 反馈监听者
     */
    void subscribe(Topic topic, Callback<?, ?> callback);

    /**
     * 通过反馈绑定主题 加载 反馈监听者
     *
     * @param topic 反馈绑定主题
     * @return 反馈监听者
     */
    Callback<?, ?> loadObservable(Topic topic);
}

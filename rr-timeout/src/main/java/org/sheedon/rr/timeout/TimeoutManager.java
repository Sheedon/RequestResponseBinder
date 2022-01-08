package org.sheedon.rr.timeout;

/**
 * 超时管理者职责，
 * 新增事件，通过ID移除事件，销毁。
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 6:08 下午
 */
public abstract class TimeoutManager<T> {

    protected OnTimeOutListener<T> listener;

    public TimeoutManager(OnTimeOutListener<T> listener) {
        this.listener = listener;
    }

    /**
     * 新增超时事件
     *
     * @param event 超时事件
     */
    public abstract void addEvent(DelayEvent<T> event);

    /**
     * 移除超时事件
     *
     * @param id 事件ID
     */
    public abstract void removeEvent(T id);

    /**
     * 销毁
     */
    public abstract void destroy();
}

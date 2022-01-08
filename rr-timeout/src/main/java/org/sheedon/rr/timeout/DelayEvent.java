package org.sheedon.rr.timeout;

/**
 * 延迟事件
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 4:59 下午
 */
public class DelayEvent<T> {

    private T id;
    private long timeOut;

    public static <T> DelayEvent<T> build(T id, long timeOut) {
        DelayEvent<T> event = new DelayEvent<>();
        event.id = id;
        event.timeOut = timeOut;
        return event;
    }

    public T getId() {
        return id;
    }

    public long getTimeOut() {
        return timeOut;
    }
}

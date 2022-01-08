package org.sheedon.rr.core;

/**
 * 时间处理行为
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 4:57 下午
 */
public interface EventBehavior {

    /**
     * 将⌚️提交到队列
     *
     * @param runnable Runnable
     */
    void pushEvent(Runnable runnable);

    /**
     * 将事件移除队列
     *
     * @param runnable Runnable
     */
    void popEvent(Runnable runnable);
}

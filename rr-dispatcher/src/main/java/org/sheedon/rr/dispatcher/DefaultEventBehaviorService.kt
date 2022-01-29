package org.sheedon.rr.dispatcher;

import org.sheedon.rr.core.EventBehavior;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 默认事件行为服务,将事件放入线程池去执行
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/9 10:41 上午
 */
public class DefaultEventBehaviorService implements EventBehavior {

    // 单线程池处理任务提交
    private final ExecutorService publishService = Executors.newSingleThreadExecutor();
    // 反馈数据，可能是高并发，使用缓存线程池
    private final ExecutorService callbackService = Executors.newCachedThreadPool();

    /**
     * 将反馈事件放入 singleThreadExecutor 中去串行执行
     *
     * @param requestRunnable Runnable 请求任务
     * @return 返回true，代表当前以被处理，无需其他事件执行者再去操作
     */
    @Override
    public boolean enqueueRequestEvent(Runnable requestRunnable) {
        publishService.execute(requestRunnable);
        return true;
    }

    /**
     * 将反馈事件放入 cachedThreadPool 中去并发执行
     *
     * @param callbackRunnable Runnable 反馈任务
     * @return 返回true，代表当前以被处理，无需其他事件执行者再去操作
     */
    @Override
    public boolean enqueueCallbackEvent(Runnable callbackRunnable) {
        callbackService.execute(callbackRunnable);
        return true;
    }
}

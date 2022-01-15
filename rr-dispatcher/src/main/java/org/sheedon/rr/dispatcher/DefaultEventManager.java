package org.sheedon.rr.dispatcher;

import org.sheedon.rr.core.BaseRequest;
import org.sheedon.rr.core.BaseResponse;
import org.sheedon.rr.core.Callback;
import org.sheedon.rr.core.EventManager;
import org.sheedon.rr.core.ReadyTask;
import org.sheedon.rr.timeout.DelayEvent;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的事件处理池，用于管理未完成的请求响应记录
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/9 2:45 下午
 */
public class DefaultEventManager<BackTopic,
        RequestData, Request extends BaseRequest<BackTopic, RequestData>,
        ResponseData, Response extends BaseResponse<BackTopic, ResponseData>>
        implements EventManager<BackTopic, String, RequestData, Request, ResponseData, Response> {

    // 以请求ID为键，以请求任务为值的请求数据池
    private final Map<String, ReadyTask<BackTopic, String, RequestData, Request, ResponseData, Response>> readyPool
            = new ConcurrentHashMap<>();
    // 主题队列池，反馈主题为键，同样的反馈主题的内容，依次存入有序队列中
    private final Map<BackTopic, Deque<String>> topicDequePool = new LinkedHashMap<>();
    // 监听反馈池
    private final Map<BackTopic, Callback<BackTopic, RequestData, Request, ResponseData, Response>> callbackPool = new ConcurrentHashMap<>();

    @Override
    public DelayEvent<String> push(Request request, Callback<BackTopic, RequestData, Request, ResponseData, Response> callback) {
        if (request == null) {
            throw new NullPointerException("request is null");
        }

        String id = UUID.randomUUID().toString();
        DelayEvent<String> event = DelayEvent.build(id, System.currentTimeMillis() + request.getDelayMilliSecond());
        // 添加准备反馈任务集合
        readyPool.put(id, ReadyTask.build(id, request, callback));
        // 添加网络反馈集合
        topicDequePool.put(request.getBackTopic(), getNetCallDeque(request.getBackTopic(), id));
        return event;
    }

    /**
     * 填充反馈集合
     *
     * @param backTopic 反馈主题
     * @param id        UUID
     * @return Deque<String>
     */
    private Deque<String> getNetCallDeque(BackTopic backTopic, String id) {
        Deque<String> callbacks = topicDequePool.get(backTopic);
        if (callbacks == null) {
            callbacks = new ArrayDeque<>();
        }

        callbacks.add(id);

        return callbacks;
    }

    /**
     * 根据反馈主题获取Callback
     *
     * @param backTopic 反馈主题
     * @return Callback<?, ?>
     */
    @Override
    public ReadyTask<BackTopic, String, RequestData, Request, ResponseData, Response> popByTopic(BackTopic backTopic) {
        synchronized (topicDequePool) {
            Deque<String> deque = topicDequePool.get(backTopic);
            if (deque == null || deque.size() == 0)
                return null;

            String id = deque.removeFirst();
            return popById(id);
        }
    }

    /**
     * 根据请求ID获取Callback
     *
     * @param id 请求ID
     * @return Callback
     */
    @Override
    public ReadyTask<BackTopic, String, RequestData, Request, ResponseData, Response> popById(String id) {
        return readyPool.remove(id);
    }

    @Override
    public boolean subscribe(BackTopic backTopic, Callback<BackTopic, RequestData, Request, ResponseData, Response> callback) {
        callbackPool.put(backTopic, callback);
        return true;
    }

    @Override
    public Callback<BackTopic, RequestData, Request, ResponseData, Response> loadObservable(BackTopic backTopic) {
        return callbackPool.get(backTopic);
    }
}

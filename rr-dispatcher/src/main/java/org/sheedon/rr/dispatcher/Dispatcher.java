package org.sheedon.rr.dispatcher;

import org.sheedon.rr.core.BaseRequest;
import org.sheedon.rr.core.BaseResponse;
import org.sheedon.rr.core.Callback;
import org.sheedon.rr.core.DispatchManager;
import org.sheedon.rr.core.EventBehavior;
import org.sheedon.rr.core.EventManager;
import org.sheedon.rr.core.ReadyTask;
import org.sheedon.rr.core.RequestAdapter;
import org.sheedon.rr.timeout.DelayEvent;
import org.sheedon.rr.timeout.TimeoutManager;

import java.util.List;

/**
 * 请求响应调度者
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 9:36 下午
 */
public class Dispatcher<BackTopic, ID> implements DispatchManager<BackTopic,ID> {

    // 事件行为服务，将任务放入服务中去执行
    private final List<EventBehavior> behaviorServices;
    // 事件池
    private final List<EventManager<BackTopic, ID>> eventManagerPool;
    // 超时处理者
    private final TimeoutManager<ID> timeoutManager;
    // 请求适配器
    private final RequestAdapter<?> requestAdapter;

    public Dispatcher(List<EventBehavior> behaviorServices,
                      List<EventManager<BackTopic, ID>> eventManagerPool,
                      TimeoutManager<ID> timeoutManager,
                      RequestAdapter<?> requestAdapter) {
        this.behaviorServices = behaviorServices;
        this.eventManagerPool = eventManagerPool;
        this.timeoutManager = timeoutManager;
        this.requestAdapter = requestAdapter;
    }

    @SuppressWarnings("unchecked")
    @Override
    public RequestAdapter<?> requestAdapter() {
        return requestAdapter;
    }

    @Override
    public void enqueueRequest(Runnable runnable) {
        for (EventBehavior service : behaviorServices) {
            if (service.enqueueRequestEvent(runnable)) {
                return;
            }
        }
    }

    @Override
    public <Request extends BaseRequest<?, BackTopic>, RRCallback extends Callback<Request, ?>>
    void addBinder(Request request, RRCallback callback) {
        for (EventManager<BackTopic, ID> manager : eventManagerPool) {
            DelayEvent<ID> event = manager.push(request, callback);
            if (event != null) {
                timeoutManager.addEvent(event);
                return;
            }
        }
    }

    @Override
    public <Request extends BaseRequest<?, BackTopic>, RRCallback extends Callback<Request, ?>>
    void addObservable(Request request, RRCallback callback) {
        for (EventManager<BackTopic, ID> manager : eventManagerPool) {
            boolean subscribed = manager.subscribe(request.getBackTopic(), callback);
            if (subscribed) {
                return;
            }
        }
    }

    @Override
    public void enqueueResponse(Runnable runnable) {
        for (EventBehavior service : behaviorServices) {
            if (service.enqueueCallbackEvent(runnable)) {
                return;
            }
        }
    }

    @Override
    public <Request extends BaseRequest<?, BackTopic>, Response extends BaseResponse<?, BackTopic>>
    void onResponse(Response response) {
        BackTopic backTopic = response.getBackTopic();
        for (EventManager<BackTopic, ID> manager : eventManagerPool) {
            ReadyTask<ID, ?> task = manager.popByTopic(backTopic);
            if (task != null) {
                timeoutManager.removeEvent(task.getId());
                //noinspection unchecked
                Request request = (Request) task.getRequest();
                //noinspection unchecked
                Callback<Request, Response> callback = (Callback<Request, Response>) task.getCallback();
                callback.onResponse(request, response);
                return;
            }
        }
    }


}

package org.sheedon.rr.dispatcher;

import org.sheedon.rr.core.BaseRequest;
import org.sheedon.rr.core.BaseResponse;
import org.sheedon.rr.core.Call;
import org.sheedon.rr.core.Callback;
import org.sheedon.rr.core.DispatchManager;
import org.sheedon.rr.core.EventBehavior;
import org.sheedon.rr.core.EventManager;
import org.sheedon.rr.core.RequestAdapter;
import org.sheedon.rr.timeout.DelayEvent;
import org.sheedon.rr.timeout.TimeoutManager;

import java.util.LinkedList;
import java.util.List;

/**
 * 请求响应调度者
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 9:36 下午
 */
public class Dispatcher<BackTopic, ID> implements DispatchManager<BackTopic> {

    // 事件行为服务，将任务放入服务中去执行
    private List<EventBehavior> behaviorServices = new LinkedList<>();
    // 事件池
    private List<EventManager<BackTopic, ID>> eventManagerPool = new LinkedList<>();
    // 超时处理者
    private TimeoutManager<ID> timeoutManager;
    // 请求适配器
    private RequestAdapter<?> requestAdapter;

    @Override
    public void enqueueRequest(Runnable runnable) {
        for (EventBehavior service : behaviorServices) {
            if (service.enqueueRequestEvent(runnable)) {
                return;
            }
        }
    }

    @Override
    public <Request extends BaseRequest<?, BackTopic>, RRCallback extends Callback<?, ?>>
    void addBinder(Request request, RRCallback callback) {
        for (EventManager<BackTopic, ID> manager : eventManagerPool) {
            DelayEvent<ID> event = manager.push(request, callback);
            if (event == null) {
                continue;
            }
            timeoutManager.addEvent(event);
        }
    }

    @Override
    public <Request extends BaseRequest<?, BackTopic>, RRCallback extends Callback<?, ?>>
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
    public <Response extends BaseResponse<?, ?>> void onResponse(Response response) {

    }


}

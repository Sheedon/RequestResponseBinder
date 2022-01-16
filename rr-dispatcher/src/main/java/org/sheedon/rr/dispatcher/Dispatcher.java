package org.sheedon.rr.dispatcher;

import org.sheedon.rr.core.BaseRequest;
import org.sheedon.rr.core.BaseResponse;
import org.sheedon.rr.core.Callback;
import org.sheedon.rr.core.DispatchManager;
import org.sheedon.rr.core.EventBehavior;
import org.sheedon.rr.core.EventManager;
import org.sheedon.rr.core.ReadyTask;
import org.sheedon.rr.core.RequestAdapter;
import org.sheedon.rr.core.ResponseAdapter;
import org.sheedon.rr.timeout.DelayEvent;
import org.sheedon.rr.timeout.OnTimeOutListener;
import org.sheedon.rr.timeout.TimeoutManager;

import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * 请求响应调度者
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 9:36 下午
 */
public class Dispatcher<BackTopic, ID,
        RequestData, Request extends BaseRequest<BackTopic, RequestData>,
        ResponseData, Response extends BaseResponse<BackTopic, ResponseData>>
        implements DispatchManager<BackTopic, ID, RequestData, Request, ResponseData, Response> {


    // 事件行为服务，将任务放入服务中去执行
    private final List<EventBehavior> behaviorServices;
    // 事件池
    private final List<EventManager<BackTopic, ID, RequestData, Request, ResponseData, Response>> eventManagerPool;
    // 超时处理者
    private final TimeoutManager<ID> timeoutManager;
    // 请求适配器
    private final RequestAdapter<RequestData> requestAdapter;
    //
    private final ResponseAdapter<BackTopic, ResponseData, Response> responseAdapter;

    public Dispatcher(List<EventBehavior> behaviorServices,
                      List<EventManager<BackTopic, ID, RequestData, Request, ResponseData, Response>> eventManagerPool,
                      TimeoutManager<ID> timeoutManager,
                      RequestAdapter<RequestData> requestAdapter,
                      ResponseAdapter<BackTopic, ResponseData, Response> responseAdapter) {
        this.behaviorServices = behaviorServices;
        this.eventManagerPool = eventManagerPool;
        this.timeoutManager = timeoutManager;
        this.requestAdapter = requestAdapter;
        this.responseAdapter = responseAdapter;

        this.timeoutManager.setListener(new TimeOutListener());
    }

    @Override
    public RequestAdapter<RequestData> requestAdapter() {
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
    public <RRCallback extends Callback<BackTopic, RequestData, Request, ResponseData, Response>>
    void addBinder(Request request, RRCallback callback) {
        for (EventManager<BackTopic, ID, RequestData, Request, ResponseData, Response> manager : eventManagerPool) {
            DelayEvent<ID> event = manager.push(request, callback);
            if (event != null) {
                timeoutManager.addEvent(event);
                return;
            }
        }
    }

    @Override
    public <RRCallback extends Callback<BackTopic, RequestData, Request, ResponseData, Response>>
    void addObservable(Request request, RRCallback callback) {
        for (EventManager<BackTopic, ID, RequestData, Request, ResponseData, Response> manager : eventManagerPool) {
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
    public void onResponse(Response response) {
        BackTopic backTopic = response.getBackTopic();
        for (EventManager<BackTopic, ID, RequestData, Request, ResponseData, Response> manager : eventManagerPool) {
            ReadyTask<BackTopic, ID, RequestData, Request, ResponseData, Response> task = manager.popByTopic(backTopic);
            if (task != null) {
                timeoutManager.removeEvent(task.getId());
                Request request = (Request) task.getRequest();
                Callback<BackTopic, RequestData, Request, ResponseData, Response> callback = task.getCallback();
                callback.onResponse(request, response);
                return;
            }
        }
    }


    /**
     * 超时监听器
     */
    private class TimeOutListener implements OnTimeOutListener<ID> {

        @Override
        public void onTimeOut(ID id, TimeoutException e) {
            for (EventManager<BackTopic, ID, RequestData, Request, ResponseData, Response> manager : eventManagerPool) {
                ReadyTask<BackTopic, ID, RequestData, Request, ResponseData, Response> task = manager.popById(id);
                if (task != null) {
                    timeoutManager.removeEvent(task.getId());
                    Request request = (Request) task.getRequest();
                    Callback<BackTopic, RequestData, Request, ResponseData, Response> callback = task.getCallback();
                    BackTopic topic = task.getRequest().getBackTopic();
                    Response failureResponse = responseAdapter.buildFailure(topic, e.getMessage());
                    callback.onResponse(request, failureResponse);
                    return;
                }
            }
        }
    }


}

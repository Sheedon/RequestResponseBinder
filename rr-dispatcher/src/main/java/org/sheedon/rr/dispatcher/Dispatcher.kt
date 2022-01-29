package org.sheedon.rr.dispatcher;

import org.sheedon.rr.core.Callback;
import org.sheedon.rr.core.DispatchAdapter;
import org.sheedon.rr.core.DispatchManager;
import org.sheedon.rr.core.EventBehavior;
import org.sheedon.rr.core.EventManager;
import org.sheedon.rr.core.IRequest;
import org.sheedon.rr.core.IResponse;
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
public class Dispatcher<BackTopic, ID, RequestData, ResponseData>
        implements DispatchManager<BackTopic, RequestData, ResponseData>,
        DispatchAdapter.OnCallListener<ResponseData> {


    // 事件行为服务，将任务放入服务中去执行
    private final List<EventBehavior> behaviorServices;
    // 事件池
    private final List<EventManager<BackTopic, ID, RequestData, ResponseData>> eventManagerPool;
    // 超时处理者
    private final TimeoutManager<ID> timeoutManager;
    // 请求适配器
    private final RequestAdapter<RequestData> requestAdapter;
    // 响应适配器
    private final ResponseAdapter<BackTopic, ResponseData> responseAdapter;

    public Dispatcher(List<EventBehavior> behaviorServices,
                      List<EventManager<BackTopic, ID, RequestData, ResponseData>> eventManagerPool,
                      TimeoutManager<ID> timeoutManager,
                      DispatchAdapter<RequestData, ResponseData> dispatchAdapter,
                      ResponseAdapter<BackTopic, ResponseData> responseAdapter) {
        this.behaviorServices = behaviorServices;
        this.eventManagerPool = eventManagerPool;
        this.timeoutManager = timeoutManager;
        this.requestAdapter = dispatchAdapter.loadRequestAdapter();
        dispatchAdapter.bindCallListener(this);
        this.responseAdapter = responseAdapter;

        this.timeoutManager.setTimeOutListener(new TimeOutListener());
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
    public void addBinder(IRequest<BackTopic, RequestData> request, Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>> callback) {
        for (EventManager<BackTopic, ID, RequestData, ResponseData> manager : eventManagerPool) {
            DelayEvent<ID> event = manager.push(request, callback);
            if (event != null) {
                timeoutManager.addEvent(event);
                return;
            }
        }
    }

    @Override
    public void addObservable(IRequest<BackTopic, RequestData> request, Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>> callback) {
        for (EventManager<BackTopic, ID, RequestData, ResponseData> manager : eventManagerPool) {
            boolean subscribed = manager.subscribe(request.backTopic(), callback);
            if (subscribed) {
                return;
            }
        }
    }

    /**
     * 执行响应反馈操作
     *
     * @param result ResponseData
     */
    @Override
    public void callResponse(final ResponseData result) {
        enqueueResponse(() -> {
            IResponse<BackTopic, ResponseData> response = responseAdapter.buildResponse(result);
            onResponse(response);
        });

    }

    /**
     * 反馈行为入队，按照预定策略去执行反馈动作
     *
     * @param runnable 反馈的Runnable
     */
    private void enqueueResponse(Runnable runnable) {
        for (EventBehavior service : behaviorServices) {
            if (service.enqueueCallbackEvent(runnable)) {
                return;
            }
        }
    }

    @Override
    public void onResponse(IResponse<BackTopic, ResponseData> response) {
        BackTopic backTopic = response.backTopic();
        for (EventManager<BackTopic, ID, RequestData, ResponseData> manager : eventManagerPool) {
            ReadyTask<BackTopic, ID, RequestData, ResponseData> task = manager.popByTopic(backTopic);
            if (task != null) {
                timeoutManager.removeEvent(task.getId());
                IRequest<BackTopic, RequestData> request = task.getRequest();
                Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>> callback
                        = task.getCallback();
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
            for (EventManager<BackTopic, ID, RequestData, ResponseData> manager : eventManagerPool) {
                ReadyTask<BackTopic, ID, RequestData, ResponseData> task = manager.popById(id);
                if (task != null) {
                    timeoutManager.removeEvent(task.getId());
                    IRequest<BackTopic, RequestData> request = task.getRequest();
                    Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>> callback
                            = task.getCallback();
                    BackTopic topic = task.getRequest().backTopic();
                    IResponse<BackTopic, ResponseData> failureResponse
                            = responseAdapter.buildFailure(topic, e.getMessage());
                    callback.onResponse(request, failureResponse);
                    return;
                }
            }
        }
    }


}

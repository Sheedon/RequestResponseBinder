package org.sheedon.rr.dispatcher;

import org.sheedon.rr.core.BaseRequest;
import org.sheedon.rr.core.BaseResponse;
import org.sheedon.rr.core.Callback;
import org.sheedon.rr.core.DispatchManager;
import org.sheedon.rr.core.Observable;

/**
 * 真实可观察者
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/14 11:04 下午
 */
public class RealObservable<BackTopic, ID,
        RequestData, Request extends BaseRequest<BackTopic, RequestData>,
        ResponseData, Response extends BaseResponse<BackTopic, ResponseData>>
        implements Observable<BackTopic, RequestData, Request, ResponseData, Response> {

    private final AbstractClient<BackTopic, ID, RequestData, Request, ResponseData, Response> client;
    // 请求对象
    private final Request originalRequest;
    // 是否执行取消操作
    private boolean canceled = false;

    protected RealObservable(AbstractClient<BackTopic, ID, RequestData, Request, ResponseData, Response> client, Request request) {
        this.client = client;
        this.originalRequest = request;
    }

    /**
     * 新增观察者
     */
    public static <BackTopic, ID,
            RequestData, Request extends BaseRequest<BackTopic, RequestData>,
            ResponseData, Response extends BaseResponse<BackTopic, ResponseData>>
    RealObservable<BackTopic, ID, RequestData, Request, ResponseData, Response>
    newRealObservable(AbstractClient<BackTopic, ID, RequestData, Request, ResponseData, Response> client, Request originalRequest) {
        // Safely publish the Call instance to the EventListener.
        //        call.eventListener = client.eventListenerFactory().create(call);
        return new RealObservable<>(client, originalRequest);
    }

    @Override
    public void subscribe(Callback<BackTopic, RequestData, Request, ResponseData, Response> callback) {
        DispatchManager<BackTopic, ID, RequestData,Request, ResponseData, Response> manager = client.getDispatchManager();
        manager.addObservable(originalRequest, callback);
    }

    @Override
    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public void cancel() {
        canceled = true;
    }
}

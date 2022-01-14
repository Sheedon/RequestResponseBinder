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
public class RealObservable<Data, Topic, ID, Request extends BaseRequest<Data, Topic>>
        implements Observable<Topic, Request> {

    private final AbstractClient<Topic, ID> client;
    // 请求对象
    private final Request originalRequest;
    // 是否执行取消操作
    private boolean canceled = false;

    protected RealObservable(AbstractClient<Topic, ID> client, Request request) {
        this.client = client;
        this.originalRequest = request;
    }

    /**
     * 新增观察者
     */
    static <Data, Topic, ID, Request extends BaseRequest<Data, Topic>> RealObservable<Data, Topic, ID, Request>
    newRealObservable(AbstractClient<Topic, ID> client, Request originalRequest) {
        // Safely publish the Call instance to the EventListener.
        //        call.eventListener = client.eventListenerFactory().create(call);
        return new RealObservable<>(client, originalRequest);
    }

    @Override
    public <Response extends BaseResponse<?, Topic>>
    void subscribe(Callback<Request, Response> callback) {
        DispatchManager<Topic, ID> manager = client.getDispatchManager();
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

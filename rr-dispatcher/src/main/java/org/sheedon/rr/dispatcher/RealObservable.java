package org.sheedon.rr.dispatcher;

import org.sheedon.rr.core.Callback;
import org.sheedon.rr.core.DispatchManager;
import org.sheedon.rr.core.IRequest;
import org.sheedon.rr.core.IResponse;
import org.sheedon.rr.core.Observable;

/**
 * 真实可观察者
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/14 11:04 下午
 */
public class RealObservable<BackTopic, ID, RequestData, ResponseData>
        implements Observable<BackTopic, RequestData, ResponseData> {

    private final AbstractClient<BackTopic, ID, RequestData, ResponseData> client;
    // 请求对象
    private final IRequest<BackTopic, RequestData> originalRequest;
    // 是否执行取消操作
    private boolean canceled = false;

    protected RealObservable(AbstractClient<BackTopic, ID, RequestData, ResponseData> client,
                             IRequest<BackTopic, RequestData> request) {
        this.client = client;
        this.originalRequest = request;
    }

    /**
     * 新增观察者
     */
    public static <BackTopic, ID, RequestData, ResponseData>
    RealObservable<BackTopic, ID, RequestData, ResponseData>
    newRealObservable(AbstractClient<BackTopic, ID, RequestData, ResponseData> client,
                      IRequest<BackTopic, RequestData> originalRequest) {
        // Safely publish the Call instance to the EventListener.
        //        call.eventListener = client.eventListenerFactory().create(call);
        return new RealObservable<>(client, originalRequest);
    }

    @Override
    public <RRCallback extends Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>>> void subscribe(RRCallback callback) {
        DispatchManager<BackTopic, RequestData, ResponseData> manager = client.getDispatchManager();
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

package org.sheedon.rr.dispatcher;

import org.sheedon.rr.core.Call;
import org.sheedon.rr.core.Callback;
import org.sheedon.rr.core.DispatchManager;
import org.sheedon.rr.core.IRequest;
import org.sheedon.rr.core.IResponse;
import org.sheedon.rr.core.NamedRunnable;
import org.sheedon.rr.core.RequestAdapter;
import org.sheedon.rr.dispatcher.model.BaseResponse;
import org.sheedon.rr.timeout.ResourceBundleUtils;


/**
 * 真实Call
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/9 5:09 下午
 */
public class RealCall<BackTopic, ID, RequestData, ResponseData>
        implements Call<BackTopic, RequestData, ResponseData> {

    private static final String BASENAME = "dispatcher";
    private static final String DISPATCHER_KEY = "request_error";

    private final AbstractClient<BackTopic, ID, RequestData, ResponseData> client;
    // 请求对象
    private final IRequest<BackTopic, RequestData> originalRequest;
    // 是否执行取消操作
    private boolean canceled = false;
    // 是否执行完成
    private boolean executed = false;

    protected RealCall(AbstractClient<BackTopic, ID, RequestData, ResponseData> client,
                       IRequest<BackTopic, RequestData> request) {
        this.client = client;
        this.originalRequest = request;
    }

    /**
     * 新增反馈类
     */
    public static <BackTopic, ID, RequestData, ResponseData>
    RealCall<BackTopic, ID, RequestData, ResponseData>
    newRealCall(AbstractClient<BackTopic, ID, RequestData, ResponseData> client,
                IRequest<BackTopic, RequestData> originalRequest) {
        // Safely publish the Call instance to the EventListener.
        //        call.eventListener = client.eventListenerFactory().create(call);
        return new RealCall<>(client, originalRequest);
    }

    @Override
    public <RRCallback extends Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>>>
    void enqueue(RRCallback callback) {
        synchronized (this) {
            if (executed) throw new IllegalStateException("Already Executed");
            executed = true;
        }
        DispatchManager<BackTopic, RequestData, ResponseData> manager = client.getDispatchManager();
        manager.enqueueRequest(new AsyncCall(client, originalRequest, callback));
    }

    @Override
    public void publish() {
        this.enqueue(null);
    }

    @Override
    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public void cancel() {
        canceled = true;
    }

    @Override
    public boolean isExecuted() {
        return executed;
    }

    final class AsyncCall extends NamedRunnable {

        private final AbstractClient<BackTopic, ID, RequestData, ResponseData> client;
        private final IRequest<BackTopic, RequestData> originalRequest;
        private final Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>> responseCallback;

        protected AsyncCall(AbstractClient<BackTopic, ID, RequestData, ResponseData> client,
                            IRequest<BackTopic, RequestData> originalRequest,
                            Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>> responseCallback) {
            super("AsyncCall %s", originalRequest);
            this.client = client;
            this.originalRequest = originalRequest;
            this.responseCallback = responseCallback;
        }

        @Override
        protected void execute() {
            if (isCanceled()) {
                return;
            }
            boolean isNeedCallback = responseCallback != null;
            DispatchManager<BackTopic, RequestData, ResponseData> manager = client.getDispatchManager();

            RequestAdapter<RequestData> adapter = manager.requestAdapter();
            RequestData body = originalRequest.body();
            body = adapter.checkRequestData(body);

            if (isNeedCallback) {
                manager.addBinder(originalRequest, responseCallback);
            }

            boolean isSuccess = adapter.publish(body);
            if (!isSuccess) {
                IResponse<BackTopic, ResponseData> response = BaseResponse.build(originalRequest.backTopic(),
                        ResourceBundleUtils.getResourceString(BASENAME, DISPATCHER_KEY));
                manager.onResponse(response);
            }


        }
    }
}

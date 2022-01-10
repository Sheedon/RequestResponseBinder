package org.sheedon.rr.dispatcher;

import org.sheedon.rr.core.BaseRequest;
import org.sheedon.rr.core.BaseResponse;
import org.sheedon.rr.core.Call;
import org.sheedon.rr.core.Callback;
import org.sheedon.rr.core.DispatchManager;
import org.sheedon.rr.core.NamedRunnable;
import org.sheedon.rr.core.RequestAdapter;
import org.sheedon.rr.timeout.ResourceBundleUtils;

/**
 * 真实Call
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/9 5:09 下午
 */
public class RealCall<Data, Topic, Request extends BaseRequest<Data, Topic>> implements Call {

    private static final String BASENAME = "dispatcher";
    private static final String DISPATCHER_KEY = "request_error";


    private final AbstractClient<Topic> client;
    // 请求对象
    private final Request originalRequest;
    // 是否执行取消操作
    private boolean canceled = false;
    // 是否执行完成
    private boolean executed = false;

    protected RealCall(AbstractClient<Topic> client, Request request) {
        this.client = client;
        this.originalRequest = request;
    }

    /**
     * 新增反馈类
     */
    static <Data, Topic, Request extends BaseRequest<Data, Topic>> RealCall<Data, Topic, Request>
    newRealCall(AbstractClient<Topic> client, Request originalRequest) {
        // Safely publish the Call instance to the EventListener.
        //        call.eventListener = client.eventListenerFactory().create(call);
        return new RealCall<>(client, originalRequest);
    }


    @SuppressWarnings({"TypeParameterHidesVisibleType", "rawtypes", "unchecked"})
    @Override
    public <Request extends BaseRequest<?, ?>, Response extends BaseResponse<Request, ?>>
    void enqueue(Callback<Request, Response> callback) {
        synchronized (this) {
            if (executed) throw new IllegalStateException("Already Executed");
            executed = true;
        }
        DispatchManager manager = client.getDispatchManager();
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

    final class AsyncCall<Response extends BaseResponse<Request, Data>> extends NamedRunnable {

        private final AbstractClient<Topic> client;
        private final Request originalRequest;
        private final Callback<Request, ?> responseCallback;

        protected AsyncCall(AbstractClient<Topic> client,
                            Request originalRequest,
                            Callback<Request, Response> responseCallback) {
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
            DispatchManager<Topic> manager = client.getDispatchManager();

            RequestAdapter<Data> adapter = manager.requestAdapter();
            Data body = originalRequest.getBody();
            body = adapter.checkRequestData(body);

            if (isNeedCallback) {
                manager.addBinder(originalRequest, responseCallback);
            }

            boolean isSuccess = adapter.publish(body);
            if (!isSuccess) {
                manager.onResponse(BaseResponse.build(originalRequest.getBackTopic(),
                        ResourceBundleUtils.getResourceString(BASENAME, DISPATCHER_KEY)));
            }


        }
    }
}

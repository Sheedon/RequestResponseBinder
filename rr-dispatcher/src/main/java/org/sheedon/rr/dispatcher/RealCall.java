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
public class RealCall<BackTopic, ID,
        RequestData, Request extends BaseRequest<BackTopic, RequestData>,
        ResponseData, Response extends BaseResponse<BackTopic, ResponseData>>
        implements Call<BackTopic, RequestData, Request, ResponseData, Response> {

    private static final String BASENAME = "dispatcher";
    private static final String DISPATCHER_KEY = "request_error";

    private final AbstractClient<BackTopic, ID, RequestData, Request, ResponseData, Response> client;
    // 请求对象
    private final Request originalRequest;
    // 是否执行取消操作
    private boolean canceled = false;
    // 是否执行完成
    private boolean executed = false;

    protected RealCall(AbstractClient<BackTopic, ID, RequestData, Request, ResponseData, Response> client, Request request) {
        this.client = client;
        this.originalRequest = request;
    }

    /**
     * 新增反馈类
     */
    public static <BackTopic, ID,
            RequestData, Request extends BaseRequest<BackTopic, RequestData>,
            ResponseData, Response extends BaseResponse<BackTopic, ResponseData>>
    RealCall<BackTopic, ID, RequestData, Request, ResponseData, Response>
    newRealCall(AbstractClient<BackTopic, ID, RequestData, Request, ResponseData, Response> client, Request originalRequest) {
        // Safely publish the Call instance to the EventListener.
        //        call.eventListener = client.eventListenerFactory().create(call);
        return new RealCall<>(client, originalRequest);
    }

    @Override
    public <RRCallback extends Callback<BackTopic, RequestData, Request, ResponseData, Response>> void enqueue(RRCallback callback) {
        synchronized (this) {
            if (executed) throw new IllegalStateException("Already Executed");
            executed = true;
        }
        DispatchManager<BackTopic, ID, RequestData, Request, ResponseData, Response> manager = client.getDispatchManager();
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

        private final AbstractClient<BackTopic, ID, RequestData, Request, ResponseData, Response> client;
        private final Request originalRequest;
        private final Callback<BackTopic, RequestData, Request, ResponseData, Response> responseCallback;

        protected AsyncCall(AbstractClient<BackTopic, ID, RequestData, Request, ResponseData, Response> client,
                            Request originalRequest,
                            Callback<BackTopic, RequestData, Request, ResponseData, Response> responseCallback) {
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
            DispatchManager<BackTopic, ID, RequestData, Request, ResponseData, Response> manager = client.getDispatchManager();

            RequestAdapter<RequestData> adapter = manager.requestAdapter();
            RequestData body = originalRequest.getBody();
            body = adapter.checkRequestData(body);

            if (isNeedCallback) {
                manager.addBinder(originalRequest, responseCallback);
            }

            boolean isSuccess = adapter.publish(body);
            if (!isSuccess) {
                //noinspection unchecked
                Response response = (Response) BaseResponse.build(originalRequest.getBackTopic(),
                        ResourceBundleUtils.getResourceString(BASENAME, DISPATCHER_KEY));
                manager.onResponse(response);
            }


        }
    }
}

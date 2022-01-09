package org.sheedon.rr.dispatcher;

import org.sheedon.rr.core.BaseRequest;
import org.sheedon.rr.core.BaseResponse;
import org.sheedon.rr.core.Call;
import org.sheedon.rr.core.Callback;
import org.sheedon.rr.core.DispatchManager;
import org.sheedon.rr.core.NamedRunnable;

/**
 * 真实Call
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/9 5:09 下午
 */
public class RealCall<Request extends BaseRequest<?, ?>> implements Call {

    // 调度管理者
    private final DispatchManager<?> manager;
    // 请求对象
    private final Request originalRequest;
    // 是否执行取消操作
    private boolean canceled = false;
    // 是否执行完成
    private boolean executed = false;

    protected RealCall(DispatchManager<?> manager, Request request) {
        this.manager = manager;
        this.originalRequest = request;
    }

    /**
     * 新增反馈类
     */
    static <Request extends BaseRequest<?, ?>> RealCall<Request>
    newRealCall(DispatchManager<?> manager, Request originalRequest) {
        // Safely publish the Call instance to the EventListener.
        //        call.eventListener = client.eventListenerFactory().create(call);
        return new RealCall<>(manager, originalRequest);
    }


    @SuppressWarnings("TypeParameterHidesVisibleType")
    @Override
    public <Request extends BaseRequest<?, ?>, Response extends BaseResponse<?, ?>>
    void enqueue(Callback<Request, Response> callback) {
        synchronized (this) {
            if (executed) throw new IllegalStateException("Already Executed");
            executed = true;
        }
//        manager.enqueueRequest();
    }

    @Override
    public void publish() {
//        manager.addObservable();
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

        private final DispatchManager<?> manager;
        private final Request originalRequest;
        private final Callback<?, ?> responseCallback;

        protected AsyncCall(DispatchManager<?> manager,
                            Request originalRequest,
                            Callback<?, ?> responseCallback) {
            super("AsyncCall %s", originalRequest);
            this.manager = manager;
            this.originalRequest = originalRequest;
            this.responseCallback = responseCallback;
        }

        @Override
        protected void execute() {

        }
    }
}

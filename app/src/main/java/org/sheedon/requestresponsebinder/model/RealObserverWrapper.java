package org.sheedon.requestresponsebinder.model;

import androidx.annotation.NonNull;

import org.sheedon.requestresponsebinder.TestMessage;
import org.sheedon.requestresponsebinder.client.BinderClient;
import org.sheedon.rr.core.Callback;
import org.sheedon.rr.core.IRequest;
import org.sheedon.rr.core.IResponse;
import org.sheedon.rr.dispatcher.RealObservable;

/**
 * 真实观察者包装类，用于调度本地构建的subscribe
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/16 4:31 下午
 */
public class RealObserverWrapper implements Observable {

    private final RealObservable<String, String, String, TestMessage> observable;

    public RealObserverWrapper(RealObservable<String, String, String, TestMessage> observable) {
        this.observable = observable;
    }

    public <Request extends IRequest<String, String>> Request request(){
        return observable.request();
    }



    public static Observable newRealObservable(BinderClient client, Request request) {
        RealObservable<String, String, String, TestMessage> realObservable = new RealObservable<>(client, request);
        return new RealObserverWrapper(realObservable);
    }

    @Override
    public <RRCallback extends Callback<IRequest<String, String>, IResponse<String, TestMessage>>> void subscribe(@NonNull RRCallback callback) {
        observable.subscribe(callback);
    }

    @SuppressWarnings("RedundantSuppression")
    @Override
    public void subscribe(org.sheedon.requestresponsebinder.model.Callback callback) {
        //noinspection unchecked,rawtypes
        this.subscribe((Callback) callback);
    }

    @Override
    public boolean isCanceled() {
        return observable.isCanceled();
    }

    @Override
    public void cancel() {
        observable.cancel();
    }
}

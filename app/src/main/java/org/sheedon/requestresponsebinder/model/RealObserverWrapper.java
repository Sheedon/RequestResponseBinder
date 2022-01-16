package org.sheedon.requestresponsebinder.model;

import org.sheedon.requestresponsebinder.TestMessage;
import org.sheedon.requestresponsebinder.client.BinderClient;
import org.sheedon.rr.core.Callback;
import org.sheedon.rr.dispatcher.RealObservable;

/**
 * java类作用描述
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/16 4:31 下午
 */
public class RealObserverWrapper implements Observable {

    private final RealObservable<String, String, String, Request, TestMessage, Response> observable;

    public RealObserverWrapper(RealObservable<String, String, String, Request, TestMessage, Response> observable) {
        this.observable = observable;
    }

    public static Observable newRealObservable(BinderClient client, Request request) {
        RealObservable<String, String, String, Request, TestMessage, Response> realObservable = RealObservable.newRealObservable(client, request);
        return new RealObserverWrapper(realObservable);
    }

    @Override
    public void subscribe(Callback<String, String, Request, TestMessage, Response> callback) {
        observable.subscribe(callback);
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

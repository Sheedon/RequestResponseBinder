package org.sheedon.requestresponsebinder.model;

import org.sheedon.requestresponsebinder.TestMessage;
import org.sheedon.requestresponsebinder.client.BinderClient;
import org.sheedon.rr.core.Callback;
import org.sheedon.rr.core.IRequest;
import org.sheedon.rr.core.IResponse;
import org.sheedon.rr.dispatcher.RealCall;

/**
 * java类作用描述
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/16 3:22 下午
 */
public class RealCallWrapper implements Call {

    private final RealCall<String, String, String, TestMessage> realCall;

    public RealCallWrapper(RealCall<String, String, String, TestMessage> realCall) {
        this.realCall = realCall;
    }

    public static Call newCall(BinderClient client, Request request) {
        RealCall<String, String, String, TestMessage> realCall = RealCall.newRealCall(client, request);
        return new RealCallWrapper(realCall);
    }

    @Override
    public <RRCallback extends Callback<IRequest<String, String>, IResponse<String, TestMessage>>>
    void enqueue(RRCallback callback) {
        realCall.enqueue(callback);
    }

    @Override
    public void enqueue(org.sheedon.requestresponsebinder.model.Callback callback) {
        //noinspection rawtypes,unchecked,unchecked
        realCall.enqueue((Callback)callback);
    }

    @Override
    public void publish() {
        realCall.publish();
    }

    @Override
    public boolean isCanceled() {
        return realCall.isCanceled();
    }

    @Override
    public void cancel() {
        realCall.cancel();
    }

    @Override
    public boolean isExecuted() {
        return realCall.isExecuted();
    }
}

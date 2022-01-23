package org.sheedon.requestresponsebinder.client;

import org.sheedon.requestresponsebinder.TestMessage;
import org.sheedon.requestresponsebinder.datamanager.TestRealClient;
import org.sheedon.requestresponsebinder.model.Call;
import org.sheedon.requestresponsebinder.model.Observable;
import org.sheedon.requestresponsebinder.model.RealCallWrapper;
import org.sheedon.requestresponsebinder.model.RealObserverWrapper;
import org.sheedon.requestresponsebinder.model.Request;
import org.sheedon.requestresponsebinder.model.Response;
import org.sheedon.rr.core.IRequest;
import org.sheedon.rr.core.RequestAdapter;
import org.sheedon.rr.core.ResponseAdapter;
import org.sheedon.rr.dispatcher.AbstractClient;
import org.sheedon.rr.dispatcher.DefaultEventBehaviorService;
import org.sheedon.rr.dispatcher.DefaultEventManager;
import org.sheedon.rr.dispatcher.Dispatcher;
import org.sheedon.rr.timeout.android.TimeOutHandler;

import java.util.Objects;

/**
 * 测试客户端类
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/14 9:21 下午
 */
public class BinderClient extends AbstractClient<String, String, String, TestMessage> {

    protected BinderClient(Builder builder) {
        super(builder);
        TestRealClient.getInstance().listener(result -> {
            Response response = responseAdapter.buildResponse(result);
            dispatcher.enqueueResponse(() -> dispatcher.onResponse(response));
        });
    }

    @Override
    public Call newCall(IRequest<String, String> request) {
        return RealCallWrapper.newCall(this, (Request) request);
    }

    @Override
    public Observable newObservable(IRequest<String, String> request) {
        return RealObserverWrapper.newRealObservable(this, (Request) request);
    }

    public static class TestRequestAdapter implements RequestAdapter<String> {

        private final String baseUrl;

        public TestRequestAdapter(String baseUrl) {
            this.baseUrl = baseUrl == null ? "" : baseUrl;
        }

        @Override
        public String checkRequestData(String message) {
            if (message == null) {
                return "11111111111";
            }

            return message;
        }

        @Override
        public boolean publish(String message) {
            if (message == null) {
                return false;
            }
            TestRealClient.getInstance().publish(message);
            return true;
        }
    }

    public static class TestResponseAdapter implements ResponseAdapter<String, TestMessage> {

        public TestResponseAdapter() {
        }

        @Override
        public Response buildFailure(String topic, String message) {
            return Response.build(topic, message);
        }

        @Override
        public Response buildResponse(TestMessage message) {
            return Response.build(message);
        }

    }


    public static class Builder extends AbstractClient.Builder<String, String, String, TestMessage> {
        // 各种其他客户端的配置
        // 例如客户端需要配置监听内容，重连机制等等

        private String baseUrl;

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = Objects.requireNonNull(baseUrl, "baseUrl == null");
            return this;
        }

        /**
         * 创建客户端
         *
         * @return BinderClient
         */
        @SuppressWarnings("unchecked")
        @Override
        protected BinderClient builder() {
            return new BinderClient(this);
        }

        @SuppressWarnings("unchecked")
        @Override
        public BinderClient build() {
            if (behaviorServices.isEmpty()) {
                behaviorServices.add(new DefaultEventBehaviorService());
            }
            if (eventManagerPool.isEmpty()) {
                eventManagerPool.add(new DefaultEventManager<>());
            }
            if (timeoutManager == null) {
                timeoutManager = new TimeOutHandler<>();
            }
            if (requestAdapter == null) {
                requestAdapter = new TestRequestAdapter(baseUrl);
            }
            if (responseAdapter == null) {
                responseAdapter = new TestResponseAdapter();
            }
            if (dispatcher == null) {
                dispatcher = new Dispatcher<>(behaviorServices, eventManagerPool,
                        timeoutManager, requestAdapter, responseAdapter);
            }
            return builder();
        }
    }

}

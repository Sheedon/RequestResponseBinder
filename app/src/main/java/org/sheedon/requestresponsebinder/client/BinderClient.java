package org.sheedon.requestresponsebinder.client;

import org.sheedon.requestresponsebinder.TestMessage;
import org.sheedon.requestresponsebinder.model.Call;
import org.sheedon.requestresponsebinder.model.Observable;
import org.sheedon.requestresponsebinder.model.RealCallWrapper;
import org.sheedon.requestresponsebinder.model.RealObserverWrapper;
import org.sheedon.requestresponsebinder.model.Request;
import org.sheedon.requestresponsebinder.model.Response;
import org.sheedon.rr.core.DataConverter;
import org.sheedon.rr.core.IRequest;
import org.sheedon.rr.core.ResponseAdapter;
import org.sheedon.rr.dispatcher.AbstractClient;
import org.sheedon.rr.dispatcher.DefaultEventBehaviorService;
import org.sheedon.rr.dispatcher.DefaultEventManager;
import org.sheedon.rr.timeout.android.TimeOutHandler;

import java.util.Objects;

/**
 * 测试客户端类
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/14 9:21 下午
 */
public class BinderClient extends AbstractClient<String /*反馈主题*/,
        String /*消息ID*/,
        String /*请求格式*/,
        TestMessage /*反馈格式*/> {

    protected BinderClient(Builder builder) {
        super(builder);
    }

    /**
     * 创建请求响应的Call
     *
     * @param request 请求对象
     * @return Call 用于执行入队/提交请求的动作
     */
    @Override
    public Call newCall(IRequest<String, String> request) {
        return RealCallWrapper.newCall(this, (Request) request);
    }

    /**
     * 创建信息的观察者 Observable
     *
     * @param request 请求对象
     * @return Observable 订阅某个主题，监听该主题的消息
     */
    @Override
    public Observable newObservable(IRequest<String, String> request) {
        return RealObserverWrapper.newRealObservable(this, (Request) request);
    }

    @SuppressWarnings("unchecked")
    public static class TestResponseAdapter implements ResponseAdapter<String, TestMessage> {

        public TestResponseAdapter() {
        }

        @Override
        public Response buildFailure(String topic, String message) {
            return Response.build(topic, message);
        }

        @Override
        public Response buildResponse(String topic, TestMessage message) {
            return Response.build(message);
        }

    }

    public static class BackTopicDataConverter implements DataConverter<TestMessage, String> {

        public BackTopicDataConverter() {
        }

        @Override
        public String convert(TestMessage value) {
            return "test";
        }
    }


    public static class Builder extends AbstractClient.Builder<BinderClient, String, String, String, TestMessage> {
        // 各种其他客户端的配置
        // 例如客户端需要配置监听内容，重连机制等等

        private String baseUrl;

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = Objects.requireNonNull(baseUrl, "baseUrl == null");
            return this;
        }

        @Override
        protected void checkAndBind() {
            if (behaviorServices.isEmpty()) {
                behaviorServices.add(new DefaultEventBehaviorService());
            }
            if (eventManagerPool.isEmpty()) {
                eventManagerPool.add(new DefaultEventManager<>());
            }
            if (timeoutManager == null) {
                timeoutManager = new TimeOutHandler<>();
            }
            if (dispatchAdapter == null) {
                dispatchAdapter = new WrapperClient(baseUrl);
            }
            if (backTopicConverter == null) {
                backTopicConverter = new BackTopicDataConverter();
            }
            if (responseAdapter == null) {
                responseAdapter = new TestResponseAdapter();
            }
        }

        /**
         * 创建客户端
         *
         * @return BinderClient
         */
        @Override
        protected BinderClient builder() {
            return new BinderClient(this);
        }
    }

}

package org.sheedon.requestresponsebinder.client;

import org.sheedon.requestresponsebinder.TestMessage;
import org.sheedon.requestresponsebinder.datamanager.TestRealClient;
import org.sheedon.requestresponsebinder.model.Response;
import org.sheedon.rr.core.DispatchAdapter;
import org.sheedon.rr.core.RequestAdapter;
import org.sheedon.rr.core.ResponseAdapter;

/**
 * java类作用描述
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/23 3:51 下午
 */
public class WrapperClient implements DispatchAdapter<String, TestMessage> {

    private DispatchAdapter.OnCallListener<TestMessage> listener;
    private final String baseUrl;

    public WrapperClient(String baseUrl) {
        this.baseUrl = baseUrl;
        TestRealClient.getInstance().listener(this::callResponse);
    }

    private WrapperClient(TestRequestAdapter requestAdapter) {
        this(requestAdapter.baseUrl);
    }

    private void callResponse(TestMessage message) {
        if (listener != null) {
            listener.callResponse(message);
        }
    }


    @Override
    public RequestAdapter<String> loadRequestAdapter() {
        return new TestRequestAdapter(baseUrl);
    }

    @Override
    public void bindCallListener(OnCallListener<TestMessage> listener) {
        this.listener = listener;
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
}

package org.sheedon.requestresponsebinder.client;

import androidx.annotation.NonNull;

import org.sheedon.requestresponsebinder.TestMessage;
import org.sheedon.requestresponsebinder.datamanager.TestRealClient;
import org.sheedon.rr.core.DispatchAdapter;
import org.sheedon.rr.core.IRequestSender;
import org.sheedon.rr.core.RequestAdapter;

/**
 * java类作用描述
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/23 3:51 下午
 */
public class WrapperClient extends DispatchAdapter.AbstractDispatchImpl<String, TestMessage> {

    private DispatchAdapter.OnCallListener<TestMessage> listener;
    private final String baseUrl;

    public WrapperClient(String baseUrl) {
        this.baseUrl = baseUrl;
        TestRealClient.getInstance().listener(this::callResponse);
    }

    private WrapperClient(TestRequestAdapter requestAdapter) {
        this(requestAdapter.baseUrl);
    }

    @NonNull
    @Override
    public RequestAdapter<String> loadRequestAdapter() {
        RequestAdapter<String> adapter = new TestRequestAdapter(baseUrl);
        adapter.bindSender(TestRealClient.getInstance());
        return adapter;
    }

    public static class TestRequestAdapter extends RequestAdapter.AbstractRequestImpl<String> {

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
            IRequestSender sender = getSender();
            if (sender instanceof TestRealClient) {
                ((TestRealClient) sender).publish(message);
            }
            return true;
        }
    }
}

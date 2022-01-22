package org.sheedon.requestresponsebinder.client;

import android.util.Log;

import org.sheedon.requestresponsebinder.model.Call;
import org.sheedon.requestresponsebinder.model.Callback;
import org.sheedon.requestresponsebinder.model.Request;
import org.sheedon.requestresponsebinder.model.Response;

/**
 * 真实请求类
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/14 10:28 下午
 */
public class RealClient {

    private static final RealClient instance = new RealClient();
    private BinderClient binderClient;

    private RealClient() {
        createBinderClient();
    }

    public static RealClient getInstance() {
        return instance;
    }

    private void createBinderClient() {
        binderClient = new BinderClient.Builder().build();
    }

    public void publish(String message) {
        Request request = new Request.RequestBuilder()
                .backTopic("test")
                .delaySecond(10)
                .body(message)
                .build();
        Call call = binderClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Throwable e) {
                Log.v("SXD", "e:" + e.getMessage());
            }

            @Override
            public void onResponse(Request request, Response response) {
                Log.v("SXD", "request:" + request.toString());
                Log.v("SXD", "response:" + response.toString());
            }
        });
    }


}

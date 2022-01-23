package org.sheedon.requestresponsebinder.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.sheedon.requestresponsebinder.client.BinderClient;
import org.sheedon.requestresponsebinder.model.Call;
import org.sheedon.requestresponsebinder.model.Callback;
import org.sheedon.requestresponsebinder.model.Request;
import org.sheedon.requestresponsebinder.model.Response;

public class MainActivity extends AppCompatActivity {

    private BinderClient binderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binderClient = new BinderClient.Builder().build();
    }

    public void onPublishClick(View view) {
        // 构建请求对象
        Request request = new Request.RequestBuilder()
                .backTopic("test")
                .delaySecond(10)
                .body("哈哈哈哈")
                .build();
        // 通过绑定的客户端获取Call
        Call call = binderClient.newCall(request);
        // 调度执行请求
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
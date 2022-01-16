package org.sheedon.requestresponsebinder.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import org.sheedon.requestresponsebinder.TestMessage;
import org.sheedon.requestresponsebinder.client.BinderClient;
import org.sheedon.requestresponsebinder.client.RealClient;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onPublishClick(View view) {
        RealClient.getInstance().publish("哈哈哈哈");

    }
}
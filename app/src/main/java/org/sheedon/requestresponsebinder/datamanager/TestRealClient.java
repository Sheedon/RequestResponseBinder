package org.sheedon.requestresponsebinder.datamanager;

import org.sheedon.requestresponsebinder.TestMessage;
import org.sheedon.rr.core.IRequestSender;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 测试服务端，用于接收数据后发送响应信息
 * 这个类不是重点
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/14 9:21 下午
 */
public class TestRealClient implements IRequestSender {

    private static final TestRealClient instance = new TestRealClient();

    // 反馈监听器
    private OnCallbackListener listener;
    // 异步处理消息
    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    private TestRealClient() {

    }

    public static TestRealClient getInstance() {
        return instance;
    }

    public void listener(OnCallbackListener listener) {
        this.listener = listener;
    }

    public void publish(String message) {
        executor.execute(new TaskRunnable(message));
    }

    public class TaskRunnable implements Runnable {

        private final String message;

        public TaskRunnable(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            try {
                TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (listener == null) {
                return;
            }

            TestMessage message = TestMessage.buildResponse(this.message);
            listener.onCallback(message);
        }
    }


    public interface OnCallbackListener {
        void onCallback(TestMessage result);
    }
}

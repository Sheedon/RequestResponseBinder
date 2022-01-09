package org.sheedon.rr.dispatcher;

import org.sheedon.rr.core.Callback;

/**
 * 准备好的任务，包含的内容请求数据和反馈Callback
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/9 2:58 下午
 */
public class ReadyTask<Data> {

    // 请求数据
    private Data requestBody;
    // 反馈Callback
    private Callback<?, ?> callback;

    public static <Data> ReadyTask<Data> build(Data requestBody, Callback<?, ?> callback) {
        ReadyTask<Data> task = new ReadyTask<>();
        task.requestBody = requestBody;
        task.callback = callback;
        return task;
    }

    public Data getRequestBody() {
        return requestBody;
    }

    public Callback<?, ?> getCallback() {
        return callback;
    }
}

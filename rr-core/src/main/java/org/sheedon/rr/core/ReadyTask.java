package org.sheedon.rr.core;

/**
 * 准备好的任务，包含的内容请求数据和反馈Callback
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/9 2:58 下午
 */
public class ReadyTask<ID, Request extends BaseRequest<?, ?>> {

    // 请求数据
    private Request request;
    // 请求记录ID
    private ID id;
    // 反馈Callback
    private Callback<Request, ?> callback;

    public static <ID, Request extends BaseRequest<?, ?>>
    ReadyTask<ID, Request> build(ID id,
                                 Request request,
                                 Callback<Request, ?> callback) {
        ReadyTask<ID, Request> task = new ReadyTask<>();
        task.request = request;
        task.id = id;
        task.callback = callback;
        return task;
    }


    public Request getRequest() {
        return request;
    }

    public Callback<Request, ?> getCallback() {
        return callback;
    }

    public ID getId() {
        return id;
    }
}

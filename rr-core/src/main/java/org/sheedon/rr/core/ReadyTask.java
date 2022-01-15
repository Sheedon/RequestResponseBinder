package org.sheedon.rr.core;

/**
 * 准备好的任务，包含的内容请求数据和反馈Callback
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/9 2:58 下午
 */
public class ReadyTask<BackTopic, ID,
        RequestData, Request extends BaseRequest<BackTopic, RequestData>,
        ResponseData, Response extends BaseResponse<BackTopic, ResponseData>> {

    // 请求数据
    private Request request;
    // 请求记录ID
    private ID id;
    // 反馈Callback
    private Callback<BackTopic, RequestData, Request, ResponseData, Response> callback;

    public static <BackTopic, ID,
            RequestData, Request extends BaseRequest<BackTopic, RequestData>,
            ResponseData, Response extends BaseResponse<BackTopic, ResponseData>>
    ReadyTask<BackTopic, ID, RequestData, Request, ResponseData, Response> build(ID id, Request request,
          Callback<BackTopic, RequestData, Request, ResponseData, Response> callback) {
        ReadyTask<BackTopic, ID, RequestData, Request, ResponseData, Response> task = new ReadyTask<>();
        task.request = request;
        task.id = id;
        task.callback = callback;
        return task;
    }


    public Request getRequest() {
        return request;
    }

    public Callback<BackTopic, RequestData, Request, ResponseData, Response> getCallback() {
        return callback;
    }

    public ID getId() {
        return id;
    }
}

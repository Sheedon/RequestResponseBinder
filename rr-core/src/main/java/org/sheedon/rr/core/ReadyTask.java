package org.sheedon.rr.core;

/**
 * 准备好的任务，包含的内容请求数据和反馈Callback
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/9 2:58 下午
 */
public class ReadyTask<BackTopic, ID, RequestData,ResponseData> {

    // 请求数据
    private IRequest<BackTopic, RequestData> request;
    // 请求记录ID
    private ID id;
    // 反馈Callback
    private Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>> callback;

    public static <BackTopic, ID,
            RequestData, Request extends IRequest<BackTopic, RequestData>,
            ResponseData>
    ReadyTask<BackTopic, ID, RequestData,ResponseData> build(ID id, Request request,
                                                Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>> callback) {
        ReadyTask<BackTopic, ID, RequestData,ResponseData> task = new ReadyTask<>();
        task.request = request;
        task.id = id;
        task.callback = callback;
        return task;
    }


    public IRequest<BackTopic, RequestData> getRequest() {
        return request;
    }

    public Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>> getCallback() {
        return callback;
    }

    public ID getId() {
        return id;
    }
}

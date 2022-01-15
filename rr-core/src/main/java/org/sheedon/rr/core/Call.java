package org.sheedon.rr.core;

/**
 * 调度的职责，包括「入队」，「提交」，「取消」操作
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 4:22 下午
 */
public interface Call<BackTopic,
        RequestData, Request extends BaseRequest<BackTopic, RequestData>,
        ResponseData, Response extends BaseResponse<BackTopic, ResponseData>> {

    /**
     * 请求入队，指代于需要将请求和反馈绑定的任务
     *
     * @param callback 反馈内容
     */
    void enqueue(Callback<BackTopic, RequestData, Request, ResponseData, Response> callback);

    /**
     * 请求提交，标志着这个请求无需监听反馈
     */
    void publish();

    /**
     * 消息是否取消
     */
    boolean isCanceled();

    /**
     * 取消任务
     */
    void cancel();

    /**
     * 是否被执行完成
     */
    boolean isExecuted();
}

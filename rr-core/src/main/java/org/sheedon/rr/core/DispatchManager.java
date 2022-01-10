package org.sheedon.rr.core;

/**
 * 作为「请求响应模式」下，请求和反馈信息的输入。
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 3:01 下午
 */
public interface DispatchManager<BackTopic> {

    <Data> RequestAdapter<Data> requestAdapter();

    /**
     * 将请求行为入队，按预定策略去执行请求动作
     *
     * @param runnable 处理事件
     */
    void enqueueRequest(Runnable runnable);

    /**
     * 添加一个请求响应关联者，用于绑定一个请求与一个响应
     *
     * @param request  请求数据
     * @param callback 反馈监听器
     */
    <Request extends BaseRequest<?, BackTopic>, RRCallback extends Callback<Request, ?>>
    void addBinder(Request request, RRCallback callback);

    /**
     * 添加一个可观察信息，用于订阅某个主题的反馈信息
     *
     * @param request  请求数据
     * @param callback 反馈监听器
     */
    <Request extends BaseRequest<?, BackTopic>, RRCallback extends Callback<Request, ?>>
    void addObservable(Request request, RRCallback callback);


    /**
     * 反馈行为入队，按照预定策略去执行反馈动作
     *
     * @param runnable 反馈的Runnable
     */
    void enqueueResponse(Runnable runnable);


    /**
     * 反馈结果监听
     *
     * @param response   反馈结果
     * @param <Response> 反馈结果
     */
    <Request extends BaseRequest<?, BackTopic>,
            Response extends BaseResponse<?, BackTopic>>
    void onResponse(Response response);


}

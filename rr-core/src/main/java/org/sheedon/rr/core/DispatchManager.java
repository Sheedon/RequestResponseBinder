package org.sheedon.rr.core;

/**
 * 作为「请求响应模式」下，请求和反馈信息的输入。
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 3:01 下午
 */
public interface DispatchManager {

    /**
     * 将请求行为入队，按预定策略去执行请求动作
     *
     * @param binder 请求响应绑定者
     */
    void enqueueRequest(Binder binder);

    /**
     * 添加一个可观察信息，用于订阅某个主题的反馈信息
     *
     * @param binder 请求响应绑定者
     */
    void addObservable(Binder binder);


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
    <Response extends BaseResponse<?, ?>> void onResponse(Response response);


}

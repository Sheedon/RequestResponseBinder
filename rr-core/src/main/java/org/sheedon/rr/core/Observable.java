package org.sheedon.rr.core;

/**
 * 订阅的职责，包括「订阅」，取消订阅
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 4:29 下午
 */
public interface Observable<BackTopic, RequestData, ResponseData> {

    /**
     * 订阅，指代于需要将请求和反馈绑定的任务
     *
     * @param callback 反馈内容
     */
    <RRCallback extends Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>>>
    void subscribe(RRCallback callback);


    /**
     * 消息是否取消
     */
    boolean isCanceled();

    /**
     * 取消任务
     */
    void cancel();
}

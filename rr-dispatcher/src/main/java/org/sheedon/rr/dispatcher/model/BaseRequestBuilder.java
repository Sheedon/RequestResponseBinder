package org.sheedon.rr.dispatcher.model;

import org.sheedon.rr.core.IRequest;

import java.util.Objects;

/**
 * 请求数据构造器，用于构造基础请求对象，包含信息与请求方法一致。
 * 反馈绑定主题、延迟时间、请求数据
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 3:16 下午
 */
public abstract class BaseRequestBuilder<Request extends BaseRequest<BackTopic, Data>, BackTopic, Data>
        implements IRequest<BackTopic, Data> {

    // 反馈绑定主题
    private BackTopic backTopic;
    // 延迟毫秒
    private long delayMilliSecond = -1;
    // 请求数据
    private Data body;

    public BaseRequestBuilder() {

    }

    public BaseRequestBuilder(Request request) {
        this.backTopic = request.backTopic();
        this.delayMilliSecond = request.delayMilliSecond();
        this.body = request.body();
    }

    @Override
    public BackTopic backTopic() {
        return backTopic;
    }

    @Override
    public long delayMilliSecond() {
        return delayMilliSecond;
    }

    @Override
    public Data body() {
        return body;
    }

    /**
     * 反馈绑定主题
     *
     * @param backTopic 反馈名
     * @return Builder
     */
    public BaseRequestBuilder<Request,BackTopic, Data> backTopic(BackTopic backTopic) {
        if (requireBackTopicNull(backTopic))
            return this;

        this.backTopic = backTopic;
        return this;
    }

    /**
     * 核实反馈数据是否为空
     *
     * @param backTopic 反馈绑定主题
     * @return 是否为空
     */
    protected abstract boolean requireBackTopicNull(BackTopic backTopic);

    /**
     * 单次请求超时额外设置
     *
     * @param delayMilliSecond 延迟时间（毫秒）
     * @return Builder
     */
    public BaseRequestBuilder<Request,BackTopic, Data> delayMilliSecond(long delayMilliSecond) {
        this.delayMilliSecond = delayMilliSecond;
        return this;
    }

    /**
     * 单次请求超时额外设置
     *
     * @param delaySecond 延迟时间（秒）
     * @return Builder
     */
    public BaseRequestBuilder<Request,BackTopic, Data> delaySecond(int delaySecond) {
        this.delayMilliSecond = delaySecond * 1000L;
        return this;
    }

    /**
     * 设置请求消息
     *
     * @param body 消息内容
     * @return Builder
     */
    public BaseRequestBuilder<Request,BackTopic, Data> body(Data body) {
        this.body = Objects.requireNonNull(body, "requestBody == null");
        return this;
    }

    public abstract Request build();
}

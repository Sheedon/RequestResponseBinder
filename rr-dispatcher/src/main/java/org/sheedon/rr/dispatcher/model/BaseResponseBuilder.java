package org.sheedon.rr.dispatcher.model;

import org.sheedon.rr.core.IResponse;

import java.util.Objects;

/**
 * 反馈数据构造器，用于构造基础反馈对象，包含信息与反馈方法一致。
 * 反馈绑定主题、反馈数据
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 3:54 下午
 */
public abstract class BaseResponseBuilder<BackTopic, Data> implements IResponse<BackTopic, Data> {

    // 反馈绑定主题
    private BackTopic backTopic;
    // 请求数据
    private Data body;

    public BaseResponseBuilder() {

    }

    public <Response extends BaseResponse<BackTopic, Data>> BaseResponseBuilder(Response response) {
        this.backTopic = response.backTopic();
        this.body = response.body();
    }

    @Override
    public BackTopic backTopic() {
        return backTopic;
    }

    @Override
    public String message() {
        return null;
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
    public BaseResponseBuilder<BackTopic, Data> backTopic(BackTopic backTopic) {
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
     * 设置反馈消息
     *
     * @param body 消息内容
     * @return Builder
     */
    public BaseResponseBuilder<BackTopic, Data> body(Data body) {
        this.body = Objects.requireNonNull(body, "responseBody == null");
        return this;
    }


    @SuppressWarnings("unchecked")
    public <Response extends BaseResponse<BackTopic, Data>> Response build() {
        return (Response) new BaseResponse<>(this);
    }

}

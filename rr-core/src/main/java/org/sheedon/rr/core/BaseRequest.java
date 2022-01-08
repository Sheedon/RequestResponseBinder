package org.sheedon.rr.core;

/**
 * 基础请求类，为了关联请求与响应的数据，必要「反馈主题」内容。
 * 除此之外，对于一个消息的超时限制，可以通过提供的超时时限来设定。
 * 其他则为实际请求数据
 *
 * <Data>: 实际请求数据model
 * <T>: 反馈主题类型
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 3:06 下午
 */
public class BaseRequest<Data, T> {

    // 反馈绑定主题
    private final T backTopic;
    // 延迟毫秒
    private final long delayMilliSecond;
    // 请求数据
    private final Data body;

    protected <RequestBuilder extends BaseRequestBuilder<Data, T>> BaseRequest(RequestBuilder builder) {
        this.backTopic = builder.getBackTopic();
        this.delayMilliSecond = builder.getDelayMilliSecond();
        this.body = builder.getBody();
    }

    public T getBackTopic() {
        return backTopic;
    }

    public long getDelayMilliSecond() {
        return delayMilliSecond;
    }

    public Data getBody() {
        return body;
    }
}

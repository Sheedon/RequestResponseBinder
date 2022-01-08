package org.sheedon.rr.core;

/**
 * 基础反馈类，需要包含的内容包括「返回主题」和「返回数据」
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 3:50 下午
 */
public class BaseResponse<Result, T> {

    // 反馈绑定主题
    private final T backTopic;
    // 请求数据
    private final Result body;

    protected <ResponseBuilder extends BaseResponseBuilder<Result, T>> BaseResponse(ResponseBuilder builder) {
        this.backTopic = builder.getBackTopic();
        this.body = builder.getBody();
    }

    public T getBackTopic() {
        return backTopic;
    }

    public Result getBody() {
        return body;
    }
}

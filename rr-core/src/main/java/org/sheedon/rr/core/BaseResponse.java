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
    private T backTopic;
    // 错误描述
    private String message;
    // 请求数据
    private Result body;

    private BaseResponse() {
    }

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

    public static <T> BaseResponse<?, T> build(T backTopic, String message) {
        BaseResponse<?, T> response = new BaseResponse<>();
        response.backTopic = backTopic;
        response.message = message;
        return response;
    }
}

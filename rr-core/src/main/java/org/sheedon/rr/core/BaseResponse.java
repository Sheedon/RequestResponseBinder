package org.sheedon.rr.core;

/**
 * 基础反馈类，需要包含的内容包括「返回主题」和「返回数据」
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 3:50 下午
 */
public class BaseResponse<BackTopic, Data> {

    // 反馈绑定主题
    private BackTopic backTopic;
    // 错误描述
    private String message;
    // 请求数据
    private Data body;

    private BaseResponse() {
    }

    protected <ResponseBuilder extends BaseResponseBuilder<BackTopic, Data>> BaseResponse(ResponseBuilder builder) {
        this.backTopic = builder.getBackTopic();
        this.body = builder.getBody();
    }

    public BackTopic getBackTopic() {
        return backTopic;
    }

    public Data getBody() {
        return body;
    }

    public static <BackTopic, Data> BaseResponse<BackTopic, Data> build(BackTopic backTopic, String message) {
        BaseResponse<BackTopic, Data> response = new BaseResponse<>();
        response.backTopic = backTopic;
        response.message = message;
        return response;
    }
}

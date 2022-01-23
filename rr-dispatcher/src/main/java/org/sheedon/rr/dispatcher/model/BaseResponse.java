package org.sheedon.rr.dispatcher.model;

import org.sheedon.rr.core.IResponse;

/**
 * 基础反馈类，需要包含的内容包括「返回主题」和「返回数据」
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 3:50 下午
 */
public class BaseResponse<BackTopic, Data> implements IResponse<BackTopic, Data> {

    // 反馈绑定主题
    private BackTopic backTopic;
    // 错误描述
    private String message;
    // 请求数据
    private Data body;



    protected BaseResponse() {
    }



    protected <ResponseBuilder extends BaseResponseBuilder<BackTopic, Data>> BaseResponse(ResponseBuilder builder) {
        this.backTopic = builder.backTopic();
        this.body = builder.body();
    }

    @Override
    public BackTopic backTopic() {
        return backTopic;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public Data body() {
        return body;
    }

    public void setBackTopic(BackTopic backTopic) {
        this.backTopic = backTopic;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setBody(Data body) {
        this.body = body;
    }

    public static <BackTopic, Data> IResponse<BackTopic, Data> build(BackTopic backTopic, String message) {
        BaseResponse<BackTopic, Data> response = new BaseResponse<>();
        response.backTopic = backTopic;
        response.message = message;
        return response;
    }
}

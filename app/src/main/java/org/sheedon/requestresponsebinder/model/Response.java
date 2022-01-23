package org.sheedon.requestresponsebinder.model;

import org.sheedon.requestresponsebinder.TestMessage;
import org.sheedon.rr.dispatcher.model.BaseResponse;
import org.sheedon.rr.dispatcher.model.BaseResponseBuilder;

/**
 * java类作用描述
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/15 11:21 上午
 */
public class Response extends BaseResponse<String, TestMessage> {

    private Response() {
        super();
    }

    public static Response build(TestMessage message) {
        Response response = new Response();
        response.setBackTopic(message.getTopic());
        response.setBody(message);
        return response;
    }

    public static class ResponseBuilder extends BaseResponseBuilder<String, TestMessage> {

        @Override
        protected boolean requireBackTopicNull(String backTopic) {
            return backTopic == null || backTopic.isEmpty();
        }
    }

    public static Response build(String backTopic, String message) {
        Response response = new Response();
        response.setBackTopic(backTopic);
        response.setMessage(message);
        return response;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "backTopic=" + backTopic() +
                ", message='" + message() + '\'' +
                ", body=" + body() +
                '}';
    }

}

package org.sheedon.requestresponsebinder.model;

import androidx.annotation.NonNull;

import org.sheedon.requestresponsebinder.TestMessage;
import org.sheedon.rr.dispatcher.model.BaseResponse;

/**
 * 协议客户端中使用的响应对象
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/15 11:21 上午
 */
public class Response extends BaseResponse<String, TestMessage> {

    public Response(String topic, TestMessage body) {
        super(topic, body);
    }

    public Response(String topic, String message) {
        super(topic, message);
    }

    public Response(String topic, String message, TestMessage body) {
        super(topic, message, body);
    }

    public static Response build(TestMessage message) {
        return new Response(message.getTopic(),message.getMessage());
    }

    public static Response build(String backTopic, String message) {
        return new Response(backTopic,message);
    }

    @NonNull
    @Override
    public String toString() {
        return "BaseResponse{" +
                "backTopic=" + backTopic() +
                ", message='" + message() + '\'' +
                ", body=" + body() +
                '}';
    }

}

package org.sheedon.requestresponsebinder.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.sheedon.requestresponsebinder.TestMessage;
import org.sheedon.rr.core.IResponse;

/**
 * 协议客户端中使用的响应对象
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/15 11:21 上午
 */
public class Response implements IResponse<String, TestMessage> {

    private final String topic;
    private String message;
    private TestMessage body;

    public Response(String topic, TestMessage body) {
        this.topic = topic;
        this.body = body;
    }

    public Response(String topic, String message) {
        this.topic = topic;
        this.message = message;
    }

    public Response(String topic, String message, TestMessage body) {
        this.topic = topic;
        this.message = message;
        this.message = message;
    }

    public static Response build(TestMessage message) {
        return new Response(message.getTopic(),message.getMessage());
    }

    public static Response build(String backTopic, String message) {
        return new Response(backTopic,message);
    }

    @Override
    public String backTopic() {
        return topic;
    }

    @Nullable
    @Override
    public String message() {
        return message;
    }

    @Override
    public TestMessage body() {
        return body;
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

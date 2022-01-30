package org.sheedon.requestresponsebinder.model;

import androidx.annotation.NonNull;

import org.sheedon.rr.dispatcher.model.BaseRequest;
import org.sheedon.rr.dispatcher.model.BaseRequestBuilder;

/**
 * 协议客户端中使用的请求对象
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/14 11:32 下午
 */
public class Request extends BaseRequest<String, String> {

    protected Request(RequestBuilder builder) {
        super(builder);
    }

    public static class RequestBuilder extends BaseRequestBuilder<Request, String, String> {

        @Override
        protected boolean requireBackTopicNull(String backTopic) {
            return backTopic == null || backTopic.isEmpty();
        }

        @Override
        public Request build() {
            return new Request(this);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "BaseRequest{" +
                "backTopic=" + backTopic() +
                ", delayMilliSecond=" + delayMilliSecond() +
                ", body=" + body() +
                '}';
    }
}

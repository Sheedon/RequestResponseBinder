package org.sheedon.requestresponsebinder.model;

import org.sheedon.rr.core.BaseRequest;
import org.sheedon.rr.core.BaseRequestBuilder;

/**
 * java类作用描述
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/14 11:32 下午
 */
public class Request extends BaseRequest<String, String> {

    protected Request(RequestBuilder builder) {
        super(builder);
    }


    public static class RequestBuilder extends BaseRequestBuilder<String, String> {

        @Override
        protected boolean requireBackTopicNull(String backTopic) {
            return backTopic == null || backTopic.isEmpty();
        }


        @SuppressWarnings("unchecked")
        @Override
        public Request build() {
            return new Request(this);
        }
    }

    @Override
    public String toString() {
        return "BaseRequest{" +
                "backTopic=" + getBackTopic() +
                ", delayMilliSecond=" + getDelayMilliSecond() +
                ", body=" + getBody() +
                '}';
    }
}

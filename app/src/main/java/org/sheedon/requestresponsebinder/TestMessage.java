package org.sheedon.requestresponsebinder;

/**
 * 测试消息格式
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/14 9:34 下午
 */
public class TestMessage {

    private String topic;
    private String message;

    public static TestMessage build(String topic, String message) {
        TestMessage testMessage = new TestMessage();
        testMessage.topic = topic;
        testMessage.message = message;
        return testMessage;
    }

    public static TestMessage buildResponse(String message) {
        TestMessage testMessage = new TestMessage();
        testMessage.topic = "test";
        testMessage.message = String.format("接收信息：%s, 处理完成", message);
        return testMessage;
    }

    public String getTopic() {
        return topic == null ? "" : topic;
    }

    public String getMessage() {
        return message == null ? "" : message;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public String toString() {
        return "TestMessage{" +
                "topic='" + topic + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}

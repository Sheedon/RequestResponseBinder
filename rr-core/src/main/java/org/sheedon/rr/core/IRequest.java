package org.sheedon.rr.core;

/**
 * 请求需要的数据内容
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/22 11:31 下午
 */
public interface IRequest<Topic, Data> extends IBackTopic<Topic> {

    /**
     * 延迟毫秒
     */
    long delayMilliSecond();

    /**
     * 请求数据
     */
    Data body();
}

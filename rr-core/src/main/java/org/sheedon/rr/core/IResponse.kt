package org.sheedon.rr.core;

/**
 * 基础反馈类，需要包含的内容包括「返回主题」和「返回数据」
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/22 11:36 下午
 */
public interface IResponse<Topic, Data> extends IBackTopic<Topic> {

    /**
     * 错误描述
     */
    String message();

    /**
     * 响应数据
     */
    Data body();

}

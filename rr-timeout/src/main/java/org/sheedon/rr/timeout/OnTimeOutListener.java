package org.sheedon.rr.timeout;

/**
 * 超时监听者，反馈超时信息的ID
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 6:11 下午
 */
public interface OnTimeOutListener<T> {

    /**
     * 超时消息
     *
     * @param id 超时消息ID
     */
    void onTimeOut(T id);

}

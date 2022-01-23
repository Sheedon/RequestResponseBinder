package org.sheedon.requestresponsebinder.model;

import org.sheedon.requestresponsebinder.TestMessage;

/**
 * 协议客户端中使用的Observable
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/15 3:55 下午
 */
public interface Observable extends org.sheedon.rr.core.Observable<String, String, TestMessage> {

    /**
     * 使用协议中的Callback ，使用默认subscribe(RRCallback callback)也可，只是泛型显示过多
     *
     * @param callback Callback
     */
    void subscribe(Callback callback);
}

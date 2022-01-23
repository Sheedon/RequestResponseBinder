package org.sheedon.requestresponsebinder.model;

import org.sheedon.requestresponsebinder.TestMessage;

/**
 * java类作用描述
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/15 3:55 下午
 */
public interface Observable extends org.sheedon.rr.core.Observable<String, String, TestMessage> {

    void subscribe(Callback callback);
}

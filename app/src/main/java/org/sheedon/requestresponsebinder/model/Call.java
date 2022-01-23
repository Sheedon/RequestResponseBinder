package org.sheedon.requestresponsebinder.model;

import org.sheedon.requestresponsebinder.TestMessage;

/**
 * java类作用描述
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/15 3:54 下午
 */
public interface Call extends org.sheedon.rr.core.Call<String, String, TestMessage> {

    void enqueue(Callback callback);
}

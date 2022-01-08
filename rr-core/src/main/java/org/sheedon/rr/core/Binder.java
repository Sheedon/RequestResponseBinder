package org.sheedon.rr.core;

/**
 * 请求响应数据绑定者
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 4:45 下午
 */
public interface Binder {

    /**
     * 加载请求对象
     *
     * @param <Request> 请求对象信息
     * @return Request
     */
    <Request extends BaseRequest<?, ?>> Request loadRequestData();

    /**
     * 加载Callback信息
     *
     * @param <RRCallback> Callback
     * @return Callback
     */
    <RRCallback extends Callback<?, ?>> RRCallback loadCallback();

}

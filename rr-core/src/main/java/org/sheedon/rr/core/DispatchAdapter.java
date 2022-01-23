package org.sheedon.rr.core;

/**
 * 调度适配器
 * 绑定请求调度和响应调度
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/23 1:35 下午
 */
public interface DispatchAdapter<RequestData, ResponseData> {

    RequestAdapter<RequestData> loadRequestAdapter();

    void bindCallListener(OnCallListener<ResponseData> listener);

    interface OnCallListener<ResponseData> {

        void callResponse(ResponseData message);
    }

}

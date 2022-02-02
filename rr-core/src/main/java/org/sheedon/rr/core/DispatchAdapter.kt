package org.sheedon.rr.core

/**
 * 调度适配器
 * 绑定请求调度和响应调度
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/23 1:35 下午
 */
interface DispatchAdapter<RequestData, ResponseData> {

    fun loadRequestAdapter(): RequestAdapter<RequestData>

    fun bindCallListener(listener: OnCallListener<ResponseData>?)

    fun callResponse(message: ResponseData)

    interface OnCallListener<ResponseData> {
        fun callResponse(message: ResponseData)
    }

    abstract class AbstractDispatchImpl<RequestData, ResponseData> :
        DispatchAdapter<RequestData, ResponseData> {

        private var listener: OnCallListener<ResponseData>? = null

        override fun bindCallListener(listener: OnCallListener<ResponseData>?) {
            this.listener = listener
        }

        override fun callResponse(message: ResponseData) {
            this.listener?.callResponse(message)
        }
    }
}
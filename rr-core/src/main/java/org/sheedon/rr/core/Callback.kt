package org.sheedon.rr.core

/**
 * 反馈消息监听
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 3:59 下午
 */
interface Callback<Request : IRequest<*, *>, Response : IResponse<*, *>> {
    /**
     * 当请求由于取消、连接问题或超时而无法执行时调用。
     * 由于网络可能在交换期间发生故障，因此远程服务器可能在故障之前接受了请求。
     */
    fun onFailure(e: Throwable)

    /**
     * 当请求成功返回，从而响应时调用。
     */
    fun onResponse(request: Request, response: Response)
}
package org.sheedon.rr.core

/**
 * 准备好的任务，包含的内容请求数据和反馈Callback
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/9 2:58 下午
 */
class ReadyTask<BackTopic, ID, RequestData, ResponseData> {
    // 请求数据
    var request: IRequest<BackTopic, RequestData>? = null
        private set

    // 请求记录ID
    var id: ID? = null
        private set

    // 反馈Callback
    var callback: Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>>? =
        null
        private set

    companion object {
        @JvmStatic
        fun <BackTopic, ID, RequestData, Request : IRequest<BackTopic, RequestData>, ResponseData> build(
            id: ID,
            request: Request,
            callback: Callback<IRequest<BackTopic, RequestData>, IResponse<BackTopic, ResponseData>>?
        ): ReadyTask<BackTopic, ID, RequestData, ResponseData> {
            val task = ReadyTask<BackTopic, ID, RequestData, ResponseData>()
            task.request = request
            task.id = id
            task.callback = callback
            return task
        }
    }
}
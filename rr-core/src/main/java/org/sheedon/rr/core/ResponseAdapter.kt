package org.sheedon.rr.core

/**
 * 结果构造器
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/16 3:34 下午
 */
interface ResponseAdapter<BackTopic, ResponseData> {

    fun <Response : IResponse<BackTopic, ResponseData>> buildFailure(
        topic: BackTopic,
        message: String
    ): Response

    fun <Response : IResponse<BackTopic, ResponseData>> buildResponse(
        topic: BackTopic,
        data: ResponseData
    ): Response
}
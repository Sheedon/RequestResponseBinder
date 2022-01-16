package org.sheedon.rr.core;

/**
 * 结果构造器
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/16 3:34 下午
 */
public interface ResponseAdapter<BackTopic, ResponseData,
        Response extends BaseResponse<BackTopic, ResponseData>> {

    Response buildFailure(BackTopic topic, String message);

    Response buildResponse(ResponseData data);
}

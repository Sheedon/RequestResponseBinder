package org.sheedon.rr.dispatcher;

import org.sheedon.rr.core.EventBehavior;
import org.sheedon.rr.core.EventManager;
import org.sheedon.rr.timeout.TimeoutManager;

import java.util.LinkedList;
import java.util.List;

/**
 * 请求响应调度者
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 9:36 下午
 */
public class Dispatcher<BackTopic, ID> {

    // 事件行为服务，将任务放入服务中去执行
    private List<EventBehavior> behaviorServices = new LinkedList<>();
    // 事件池
    private List<EventManager<BackTopic, ID>> eventManagerPool = new LinkedList<>();
    // 超时处理者
    private TimeoutManager<ID> timeoutManager;



}

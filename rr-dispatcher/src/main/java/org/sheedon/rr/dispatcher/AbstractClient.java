package org.sheedon.rr.dispatcher;

import org.sheedon.rr.core.DispatchManager;
import org.sheedon.rr.core.EventBehavior;
import org.sheedon.rr.core.EventManager;
import org.sheedon.rr.core.RequestAdapter;
import org.sheedon.rr.timeout.TimeoutManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * 抽象客户端类，需要转化的协议继承自当前类，来实现基本配置
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/9 10:35 上午
 */
public class AbstractClient<BackTopic, ID> {

    private final DispatchManager<BackTopic, ID> dispatcher;
    private final int timeout;

    protected AbstractClient(Builder<BackTopic, ID> builder) {
        this.dispatcher = builder.dispatcher;
        this.timeout = builder.timeout;
    }

    public DispatchManager<BackTopic, ID> getDispatchManager() {
        return dispatcher;
    }

    public int getTimeout() {
        return timeout;
    }

    public static abstract class Builder<BackTopic, ID> {
        protected DispatchManager<BackTopic, ID> dispatcher;
        protected int timeout;
        // 职责服务执行环境
        protected List<EventBehavior> behaviorServices = new LinkedList<>();
        // 事件管理者集合
        protected List<EventManager<BackTopic, ID>> eventManagerPool = new LinkedList<>();
        // 超时处理者
        protected TimeoutManager<ID> timeoutManager;
        // 请求适配器
        protected RequestAdapter<?> requestAdapter;

        public Builder() {
            timeout = 5;
        }

        /**
         * 设置用于设置策略和执行异步请求的调度程序。不能为null。
         *
         * @param dispatcher 请求响应执行者
         * @return Builder<BackTopic, ID> 构建者
         */
        public Builder<BackTopic, ID> dispatcher(Dispatcher<BackTopic, ID> dispatcher) {
            this.dispatcher = Objects.requireNonNull(dispatcher, "dispatcher == null");
            return this;
        }

        /**
         * 设置信息请求超时时间（单位秒）
         *
         * @param timeout 超时时间
         * @return Builder<BackTopic, ID> 构建者
         */
        public Builder<BackTopic, ID> messageTimeout(int timeout) {
            if (timeout < 0)
                return this;

            this.timeout = timeout;
            return this;
        }

        /**
         * 设置行为服务环境集合
         *
         * @param behaviorServices 执行服务环境集合
         * @return Builder<BackTopic, ID>
         */
        public Builder<BackTopic, ID> behaviorServices(List<EventBehavior> behaviorServices) {
            this.behaviorServices = Objects.requireNonNull(behaviorServices, "behaviorServices == null");
            return this;
        }

        /**
         * 设置行为线程池，后加的靠前
         *
         * @param behaviorService 执行服务环境
         * @return Builder<BackTopic, ID>
         */
        public Builder<BackTopic, ID> behaviorService(EventBehavior behaviorService) {
            EventBehavior behavior = Objects.requireNonNull(behaviorService, "behaviorService == null");
            this.behaviorServices.add(0, behavior);
            return this;
        }

        /**
         * 设置事件管理集合
         *
         * @param eventManagerPool 事件管理集合
         * @return Builder<BackTopic, ID>
         */
        public Builder<BackTopic, ID> eventManagerPool(List<EventManager<BackTopic, ID>> eventManagerPool) {
            this.eventManagerPool = Objects.requireNonNull(eventManagerPool, "eventManagerPool == null");
            return this;
        }

        /**
         * 设置事件管理
         *
         * @param eventManager 事件管理
         * @return Builder<BackTopic, ID>
         */
        public Builder<BackTopic, ID> eventManager(EventManager<BackTopic, ID> eventManager) {
            EventManager<BackTopic, ID> manager = Objects.requireNonNull(eventManager, "eventManager == null");
            this.eventManagerPool.add(0, manager);
            return this;
        }

        /**
         * 设置超时处理者
         *
         * @param timeoutManager 事件管理
         * @return Builder<BackTopic, ID>
         */
        public Builder<BackTopic, ID> timeoutManager(TimeoutManager<ID> timeoutManager) {
            this.timeoutManager = Objects.requireNonNull(timeoutManager, "timeoutManager == null");
            return this;
        }

        /**
         * 设置请求调度适配者
         *
         * @param requestAdapter 请求调度适配者
         * @return Builder<BackTopic, ID>
         */
        public Builder<BackTopic, ID> requestAdapter(RequestAdapter<?> requestAdapter) {
            this.requestAdapter = Objects.requireNonNull(requestAdapter, "requestAdapter == null");
            return this;
        }


        public <Client extends AbstractClient<BackTopic, ID>> Client build() {
            if(behaviorServices.isEmpty()){
                behaviorServices.add(new DefaultEventBehaviorService());
            }
            if(eventManagerPool.isEmpty()){
                throw new NullPointerException("please eventManager(new DefaultEventManager())");
            }
            if(timeoutManager == null){
                throw new NullPointerException("please add timeoutManager()");
            }
            if(requestAdapter == null){
                throw new NullPointerException("please add requestAdapter()");
            }
            if (dispatcher == null) {
                dispatcher = new Dispatcher<>(behaviorServices, eventManagerPool,
                        timeoutManager, requestAdapter);
            }
            return builder();
        }

        protected abstract <Client extends AbstractClient<BackTopic, ID>> Client builder();
    }
}

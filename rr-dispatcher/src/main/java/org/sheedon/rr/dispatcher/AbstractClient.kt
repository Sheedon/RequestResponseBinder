package org.sheedon.rr.dispatcher

import org.sheedon.rr.core.*
import org.sheedon.rr.core.Observable
import org.sheedon.rr.timeout.TimeoutManager
import java.lang.NullPointerException
import java.util.*

/**
 * 抽象客户端类，需要转化的协议继承自当前类，来实现基本配置
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/9 10:35 上午
 */
abstract class AbstractClient<BackTopic, ID, RequestData, ResponseData> protected constructor(
    builder: Builder<BackTopic, ID, RequestData, ResponseData>
) {
    @get:JvmName("dispatchManager")
    val dispatchManager: DispatchManager<BackTopic, RequestData, ResponseData> =
        builder.dispatcher!!

    @get:JvmName("timeout")
    val timeout: Int = builder.timeout

    open fun newCall(request: IRequest<BackTopic, RequestData>): Call<BackTopic, RequestData, ResponseData> {
        return RealCall(this, request)
    }

    open fun newObservable(request: IRequest<BackTopic, RequestData>): Observable<BackTopic, RequestData, ResponseData>? {
        return RealObservable(this, request)
    }

    protected abstract class Builder<BackTopic, ID, RequestData, ResponseData> {
        internal var dispatcher: DispatchManager<BackTopic, RequestData, ResponseData>? = null
        internal var timeout = 5

        // 职责服务执行环境
        @JvmField
        protected var behaviorServices: MutableList<EventBehavior> = LinkedList()

        // 事件管理者集合
        @JvmField
        protected var eventManagerPool: MutableList<EventManager<BackTopic, ID, RequestData, ResponseData>> =
            LinkedList()

        // 超时处理者
        @JvmField
        protected var timeoutManager: TimeoutManager<ID>? = null

        // 调度适配器
        @JvmField
        protected var dispatchAdapter: DispatchAdapter<RequestData, ResponseData>? = null

        // 反馈适配器
        @JvmField
        protected var responseAdapter: ResponseAdapter<BackTopic, ResponseData>? = null

        /**
         * 设置用于设置策略和执行异步请求的调度程序。不能为null。
         *
         * @param dispatcher 请求响应执行者
         */
        fun dispatcher(dispatcher: Dispatcher<BackTopic, ID, RequestData, ResponseData>) = apply {
            this.dispatcher = dispatcher
        }

        /**
         * 设置信息请求超时时间（单位秒）
         *
         * @param timeout 超时时间
         * @return Builder<BackTopic></BackTopic>, ID> 构建者
         */
        fun messageTimeout(timeout: Int) = apply {
            if (timeout < 0) return this
            this.timeout = timeout
        }

        /**
         * 设置行为服务环境集合
         *
         * @param behaviorServices 执行服务环境集合
         * @return Builder<BackTopic></BackTopic>, ID>
         */
        fun behaviorServices(behaviorServices: MutableList<EventBehavior>) = apply {
            this.behaviorServices = behaviorServices
        }

        /**
         * 设置行为线程池，后加的靠前
         *
         * @param behaviorService 执行服务环境
         * @return Builder<BackTopic></BackTopic>, ID>
         */
        fun behaviorService(behaviorService: EventBehavior) = apply {
            behaviorServices.add(0, behaviorService)
        }

        /**
         * 设置事件管理集合
         *
         * @param eventManagerPool 事件管理集合
         * @return Builder<BackTopic></BackTopic>, ID>
         */
        fun eventManagerPool(eventManagerPool: MutableList<EventManager<BackTopic, ID, RequestData, ResponseData>>) =
            apply {
                this.eventManagerPool = eventManagerPool
            }

        /**
         * 设置事件管理
         *
         * @param eventManager 事件管理
         * @return Builder<BackTopic></BackTopic>, ID>
         */
        fun eventManager(eventManager: EventManager<BackTopic, ID, RequestData, ResponseData>) =
            apply {
                eventManagerPool.add(0, eventManager)
            }

        /**
         * 设置超时处理者
         *
         * @param timeoutManager 事件管理
         * @return Builder<BackTopic></BackTopic>, ID>
         */
        fun timeoutManager(timeoutManager: TimeoutManager<ID>) = apply {
            this.timeoutManager = timeoutManager
        }

        /**
         * 设置调度调度适配者
         *
         * @param dispatchAdapter 调度适配者
         * @return Builder<BackTopic></BackTopic>, ID>
         */
        fun dispatchAdapter(dispatchAdapter: DispatchAdapter<RequestData, ResponseData>) = apply {
            requireNotNull(dispatchAdapter.loadRequestAdapter()) { "requestAdapter == null" }
            this.dispatchAdapter = dispatchAdapter
        }

        /**
         * 设置响应调度适配者
         *
         * @param responseAdapter 响应调度适配者
         * @return Builder<BackTopic></BackTopic>, ID>
         */
        fun responseAdapter(responseAdapter: ResponseAdapter<BackTopic, ResponseData>) = apply {
            this.responseAdapter = responseAdapter
        }

        fun <Client : AbstractClient<BackTopic, ID, RequestData, ResponseData>> build(): Client {
            checkAndBind()
            buildDispatcher()
            return builder()
        }

        protected open fun checkAndBind() {
            if (behaviorServices.isEmpty()) {
                behaviorServices.add(DefaultEventBehaviorService())
            }
            if (eventManagerPool.isEmpty()) {
                throw NullPointerException("please eventManager(new DefaultEventManager())")
            }
            if (timeoutManager == null) {
                throw NullPointerException("please add timeoutManager()")
            }
            if (dispatchAdapter == null) {
                throw NullPointerException("please add dispatchAdapter()")
            }
            if (responseAdapter == null) {
                throw NullPointerException("please add responseAdapter()")
            }
        }

        private fun buildDispatcher() {
            if (dispatcher == null) {
                dispatcher = Dispatcher(
                    behaviorServices, eventManagerPool,
                    timeoutManager!!, dispatchAdapter!!, responseAdapter!!
                )
            }
        }

        protected abstract fun <Client : AbstractClient<BackTopic, ID, RequestData, ResponseData>?> builder(): Client
    }
}
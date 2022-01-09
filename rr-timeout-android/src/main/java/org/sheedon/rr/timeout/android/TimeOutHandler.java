package org.sheedon.rr.timeout.android;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import org.sheedon.rr.timeout.DelayEvent;
import org.sheedon.rr.timeout.OnTimeOutListener;
import org.sheedon.rr.timeout.ResourceBundleUtils;
import org.sheedon.rr.timeout.TimeoutManager;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.TimeoutException;

/**
 * 超时执行者，处理超时事件发送
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 6:33 下午
 */
public class TimeOutHandler<T> extends TimeoutManager<T> {

    private static String TIMEOUT;
    private static final String BASENAME = "timeout";
    private static final String RESOURCE_KEY = "data_time_out";

    // 处理线程
    private final HandlerThread triggerThread;
    // 事务处理器
    private final Handler workHandler;

    public TimeOutHandler(OnTimeOutListener<T> listener) {
        this(TimeOutHandler.class.getCanonicalName(), listener);
    }

    public TimeOutHandler(String name, OnTimeOutListener<T> listener) {
        super(listener);
        TIMEOUT = ResourceBundleUtils.getResourceString(BASENAME, RESOURCE_KEY);

        // 创建一个HandlerThread 用于执行消息Loop
        triggerThread = new HandlerThread(name);
        triggerThread.start();

        // 创建绑定在triggerThread的handler
        workHandler = new Handler(triggerThread.getLooper(), msg -> {
            if (listener != null) {
                //noinspection unchecked
                listener.onTimeOut((T) msg.obj,
                        new TimeoutException(TIMEOUT));
            }
            return false;
        });
    }


    /**
     * 新增超时事件
     *
     * @param event 超时事件
     */
    @Override
    public void addEvent(DelayEvent<T> event) {
        Message obtain = Message.obtain();
        obtain.obj = event.getId();
        workHandler.sendMessageDelayed(obtain, event.getTimeOut() - System.currentTimeMillis());
    }


    /**
     * 移除超时事件
     *
     * @param id 超时事件Id
     */
    @Override
    public void removeEvent(T id) {
        workHandler.removeCallbacksAndMessages(id);
    }

    /**
     * 销毁
     */
    @Override
    public void destroy() {
        workHandler.removeCallbacksAndMessages(null);
        if (triggerThread != null) {
            triggerThread.quitSafely();
        }
    }
}

package org.sheedon.rr.core;

import java.util.Locale;

/**
 * 设置其线程名称的 Runnable 实现。
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 4:09 下午
 */
public abstract class NamedRunnable implements Runnable {

    private final String name;

    NamedRunnable(String format, Object... args) {
        this.name = String.format(Locale.US, format, args);
    }

    @Override
    public final void run() {
        String oldName = Thread.currentThread().getName();
        Thread.currentThread().setName(name);
        try {
            execute();
        } finally {
            Thread.currentThread().setName(oldName);
        }
    }

    protected abstract void execute();
}

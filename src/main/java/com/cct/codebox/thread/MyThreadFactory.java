package com.cct.codebox.thread;

import lombok.AllArgsConstructor;

import java.util.concurrent.ThreadFactory;

/**
 * @BelongsProject: waterChat
 * @Author: cct
 * @Description: TODO
 */
@AllArgsConstructor
public class MyThreadFactory implements ThreadFactory {

    private static final  MyThreadUncaughtExceptionHandler MY_UNCAUGHT_EXCEPTION_HANDLER = new MyThreadUncaughtExceptionHandler();
    private ThreadFactory originFactory;
    @Override
    public Thread newThread(Runnable r) {
        Thread thread = originFactory.newThread(r);
        //在spring自带factory生成的thread上添加setUncaughtExceptionHandler
        thread.setUncaughtExceptionHandler(MY_UNCAUGHT_EXCEPTION_HANDLER);
        return thread;
    }
}

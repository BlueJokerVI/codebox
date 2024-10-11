package com.cct.codebox.thread;

import lombok.extern.slf4j.Slf4j;

/**
 * @BelongsProject: waterChat
 * @Author: cct
 * @Description: 自定义线程异常捕获处理器（处理线程未捕获的异常）
 */
@Slf4j
public class MyThreadUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println(t);
        System.out.println(e);
        log.error("ThreadName:{},   Cause:{}", t.getName(), e.getMessage());
    }
}

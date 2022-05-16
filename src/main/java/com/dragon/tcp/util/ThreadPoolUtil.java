package com.dragon.tcp.util;

import java.util.concurrent.*;

/**
 * @author xuejingbao
 * @create 2021-12-15 9:50
 */
public class ThreadPoolUtil {

    private static ThreadPoolExecutor threadPool;

    /**
     * 无返回值直接执行
     * @param runnable
     */
    public  static void execute(Runnable runnable){
        getThreadPool().execute(runnable);
    }

    /**
     * 返回值直接执行
     * @param callable
     */
    public  static <T> Future<T> submit(Callable<T> callable){
        return   getThreadPool().submit(callable);
    }

    public static ThreadFactory getThreadFactory(){
        if (threadPool == null) {
            getThreadPool();
        }
        return threadPool.getThreadFactory();
    }


    /**
     * dcs获取线程池
     * @return 线程池对象
     */
    public static ThreadPoolExecutor getThreadPool() {
        if (threadPool != null) {
            return threadPool;
        } else {
            synchronized (ThreadPoolUtil.class) {
                if (threadPool == null) {
                    threadPool = new ThreadPoolExecutor(8,
                            16,
                            60,
                            TimeUnit.SECONDS,
                            new LinkedBlockingQueue<>(32),
                            new ThreadPoolExecutor.CallerRunsPolicy());
                }
                return threadPool;
            }
        }
    }



}

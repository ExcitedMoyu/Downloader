package com.smasher.downloader.execute;

import com.smasher.downloader.entity.DownloadInfo;
import com.smasher.downloader.task.DownloadTask;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author moyu
 */
public class DownloadExecutor extends ThreadPoolExecutor {

    /**
     * 关于线程池的一些配置
     */
    public static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    public static final int CORE_POOL_SIZE = Math.max(3, CPU_COUNT / 2);
    public static final int MAX_POOL_SIZE = CORE_POOL_SIZE * 2;
    public static final long KEEP_ALIVE_TIME = 0L;

    public static final String TAG = "DownloadExecutor";

    public DownloadExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                            TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public void executeTask(DownloadTask task) {
        DownloadInfo info = task.getDownloadInfo();
        if (!task.isRunning()) {
            info.setStatus(DownloadInfo.JS_STATE_WAIT);
            execute(task);
        }
    }


    public void executeRunnable(Runnable runnable) {
        execute(runnable);
    }


    public static DownloadExecutor getDefaultExecutor() {
        return new DownloadExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>());
    }


}

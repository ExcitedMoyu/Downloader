package com.smasher.downloader.execute;


import com.smasher.downloader.entity.DownloadInfo;
import com.smasher.downloader.task.DownloadTask;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author moyu
 */
public class DownloadExecutor extends ThreadPoolExecutor {

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
}

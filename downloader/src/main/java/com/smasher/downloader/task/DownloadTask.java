package com.smasher.downloader.task;

import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.smasher.downloader.entity.DownloadInfo;
import com.smasher.downloader.handler.WeakReferenceHandler;
import com.smasher.downloader.manager.DownloadManager;
import com.smasher.downloader.manager.OkHttpClientManager;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 下载
 *
 * @author matao
 * @date 2017/3/16
 */
public class DownloadTask implements Runnable {

    private static final String TAG = "[DL]DownloadTask";

    private DownloadInfo mDownloadInfo;
    private OkHttpClient mClient;
    private WeakReferenceHandler mHandler;


    public DownloadTask(DownloadInfo info, WeakReferenceHandler handler) {
        this.mDownloadInfo = info;
        this.mHandler = handler;
        this.mClient = OkHttpClientManager.getInstance().getClient();
    }


    /**
     * 暂停
     */
    public void stop() {
        mDownloadInfo.setStatus(DownloadInfo.JS_STATE_PAUSE);
    }


    @Override
    public void run() {
        mDownloadInfo.setStatus(DownloadInfo.JS_STATE_DOWNLOAD_PRE);
        sendMessage();

        File target;
        //获取文件长度
        long contentLength = getContentLength(mDownloadInfo);
        if (contentLength > 0) {
            mDownloadInfo.setStatus(DownloadInfo.JS_STATE_DOWNLOADING);
            sendMessage();
            target = checkLocalFile(mDownloadInfo);
        } else {
            mDownloadInfo.setStatus(DownloadInfo.JS_STATE_FAILED);
            sendMessage();
            return;
        }

        downLoadAPK(mDownloadInfo, target);
    }


    /**
     * 獲取下載文件大小
     *
     * @param downloadInfo 下载任务信息
     * @return 下載apk文件大小
     */
    private long getContentLength(DownloadInfo downloadInfo) {

        //获取文件长度
        long contentLength = 0;
        if (downloadInfo.getTotal() == 0) {
            Request request = new Request.Builder()
                    .url(downloadInfo.getUrl())
                    .build();
            try {
                Response response = mClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        contentLength = response.body().contentLength();
                    }
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "getContentLength: error");
            }
            downloadInfo.setTotal(contentLength);
        } else {
            return downloadInfo.getTotal();
        }
        Log.d(TAG, "getContentLength: " + contentLength);
        return contentLength;
    }


    /**
     * 检测本地文件
     *
     * @param downloadInfo 下载任务信息
     */
    private File checkLocalFile(DownloadInfo downloadInfo) {
        Log.d(TAG, "checkLocalFile: check local file start...");

        long downloadLength = 0;

        long totalLength = downloadInfo.getTotal();

        String targetPath = getSavePath(downloadInfo);

        File mFile = new File(targetPath);

        String fileNameTemp = downloadInfo.getFullName();

        File file = new File(mFile, fileNameTemp);

        if (file.exists()) {
            //找到了文件,代表已经下载过,则获取其长度
            downloadLength = file.length();

            //之前下载过,需要重新来一个文件
            while (downloadLength >= totalLength) {

                if (file.delete()) {
                    downloadLength = 0;
                }
            }
        }

        downloadInfo.setProgress(downloadLength);

        checkTargetFile(file);

        return file;
    }


    /**
     * 检查目标文件是否存在
     *
     * @param target target
     */
    private void checkTargetFile(File target) {
        File parent = target.getParentFile();
        if (!parent.exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            if (!parent.mkdirs()) {
                Log.e(TAG, "checkTargetFile: create dir error");
            } else {
                if (!target.exists()) {
                    try {
                        boolean isCreate = target.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            if (!target.exists()) {
                try {
                    boolean isCreate = target.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 下载APK文件
     *
     * @param downloadInfo 下载任务信息
     * @param saveFile     saveFile
     */
    private void downLoadAPK(DownloadInfo downloadInfo, File saveFile) {

        Log.d(TAG, "downLoadAPK: downloading...");
        downloadInfo.setStatus(DownloadInfo.JS_STATE_DOWNLOADING);

        InputStream is = null;
        RandomAccessFile accessFile = null;

        try {
            String url = downloadInfo.getUrl();
            long headDownloadLength = downloadInfo.getProgress();
            //文件的总长度
            long headContentLength = downloadInfo.getTotal();

            //确定下载的范围,添加此头,则服务器就可以跳过已经下载好的部分
            Request downRequest = new Request.Builder()
                    .addHeader("RANGE", "bytes=" + headDownloadLength + "-" + headContentLength)
                    .url(url)
                    .build();
            Call call = mClient.newCall(downRequest);
            Response response = call.execute();
            if (response.body() == null) {
                return;
            }

            is = response.body().byteStream();
            accessFile = new RandomAccessFile(saveFile, "rws");
            accessFile.seek(headDownloadLength);

            //缓冲数组2kB
            byte[] buffer = new byte[1024 * 8];

            long time = System.currentTimeMillis();
            int offset;
            int percent = 0;
            int percentLast = 0;
            while ((offset = is.read(buffer)) != -1) {

                if (isPaused(downloadInfo)) {
                    //暂停
                    accessFile.close();
                    is.close();
                    sendMessage();
                    return;
                }

                //下载增量进度大于3%并且时间大于2秒更新一次
                if (frequency(percent, percentLast) && System.currentTimeMillis() - time > 2000) {
                    time = System.currentTimeMillis();
                    percentLast = percent;
                    sendMessage();
                }

                accessFile.write(buffer, 0, offset);
                downloadInfo.setProgress(downloadInfo.getProgress() + offset);
                percent = getPercent(downloadInfo);
            }

            //下载完成
            downloadInfo.setStatus(DownloadInfo.JS_STATE_FINISH);
            sendMessage();
        } catch (Exception e) {
            if (downloadInfo.getStatus() != DownloadInfo.JS_STATE_PAUSE) {
                downloadInfo.setStatus(DownloadInfo.JS_STATE_FAILED);
                sendMessage();
            } else {
                sendMessage();
            }
        } finally {
            //关闭IO流
            closeAll(is, accessFile);
        }
    }

    private int getPercent(DownloadInfo downloadInfo) {
        return (int) (((float) downloadInfo.getProgress() * 100 / downloadInfo.getTotal()));
    }

    /**
     * 控制更新频次
     * 下载更新进度大于3%
     *
     * @param percent     当前进度
     * @param percentLast 上次通知进度
     * @return boolean
     */
    private boolean frequency(int percent, int percentLast) {
        if (percent == percentLast) {
            return false;
        }
        return percent - percentLast > 3;
    }

    private boolean isPaused(DownloadInfo downloadInfo) {
        return downloadInfo.getStatus() == DownloadInfo.JS_STATE_PAUSE;
    }


    private static String getSavePath(DownloadInfo downloadInfo) {
        if (!TextUtils.isEmpty(downloadInfo.getTargetPath())) {
            return downloadInfo.getTargetPath();
        }
        return DownloadManager.getInstance().getSavePath();
    }


    /**
     * 发出消息更新通知
     */
    private void sendMessage() {
        Message message = Message.obtain();
        message.what = mDownloadInfo.getStatus();
        message.obj = mDownloadInfo;
        if (mHandler != null) {
            mHandler.sendMessage(message);
        }
    }


    public DownloadInfo getDownloadInfo() {
        return mDownloadInfo;
    }


    private void closeAll(Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isRunning() {
        return mDownloadInfo.getStatus() == DownloadInfo.JS_STATE_DOWNLOADING ||
                mDownloadInfo.getStatus() == DownloadInfo.JS_STATE_DOWNLOAD_PRE ||
                mDownloadInfo.getStatus() == DownloadInfo.JS_STATE_GET_TOTAL ||
                mDownloadInfo.getStatus() == DownloadInfo.JS_STATE_WAIT;

    }
}

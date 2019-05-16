package com.smasher.downloader.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import android.text.TextUtils;
import android.util.Log;

import com.smasher.downloader.entity.DownloadInfo;
import com.smasher.downloader.manager.DownloadManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * utils
 *
 * @author matao
 * @date 2017/8/15
 */
public class DownloadUtils {
    private static final String TAG = "[DL]DownloadUtils";
    private static final int DOWNLOAD_PERCENT_SUCCESS = 100;


    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while ((len = inStream.read(buffer)) != -1) {
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        inStream.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }


    /**
     * 安装
     *
     * @param downloadInfo 下载任务信息
     */
    public static void install(Context context, DownloadInfo downloadInfo) {
        try {
            String targetPath = getSavePath(downloadInfo);
            String fullName = downloadInfo.getFullName();

            File parent = new File(targetPath);
            File target = new File(parent, fullName);

            Intent intentIns = new Intent(Intent.ACTION_VIEW);
            intentIns.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                String applicationProvider = context.getPackageName() + ".provider";
                intentIns.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(context, applicationProvider, target);
                intentIns.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intentIns.setDataAndType(Uri.fromFile(target), "application/vnd.android.package-archive");
            }
            context.startActivity(intentIns);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param downloadInfo 下载信息
     * @return 文件长度
     */
    public static long getFileSize(DownloadInfo downloadInfo) {
        String fullName = downloadInfo.getFullName();
        String targetPath = getSavePath(downloadInfo);
        if (TextUtils.isEmpty(targetPath)) {
            Log.e(TAG, "save path is empty!");
            return 0;
        }

        File parent = new File(targetPath);
        File target = new File(parent, fullName);
        if (target.exists() && target.isFile()) {
            return target.length();
        }
        return 0;
    }


    private static String getSavePath(DownloadInfo downloadInfo) {
        if (!TextUtils.isEmpty(downloadInfo.getTargetPath())) {
            return downloadInfo.getTargetPath();
        }
        return DownloadManager.getInstance().getSavePath();

    }


    // region #废弃的

    @Deprecated
    public static void checkStatus(Context context, DownloadInfo mInfo, boolean contains) {
        //是否已经安装
        if (isInstalled(context, mInfo)) {
            mInfo.setStatus(DownloadInfo.JS_STATE_INSTALLED);
        } else if (DownloadUtils.getFileSize(mInfo) > 0) {
            int percent = getUnCacheProgress(context, mInfo.getFullName(), DownloadUtils.getFileSize(mInfo));
            if (percent == DOWNLOAD_PERCENT_SUCCESS) {
                mInfo.setStatus(DownloadInfo.JS_STATE_FINISH);
            } else {
                if (contains) {
                    //do nothing
                } else {
                    mInfo.setStatus(DownloadInfo.JS_STATE_PAUSE);
                }
            }
        } else {
            mInfo.setStatus(DownloadInfo.JS_STATE_NORMAL);
        }
    }


    /**
     * 获取未在downInfo列表里的下载信息
     *
     * @param uniqueKey packageName
     * @param fileSize  fileSize
     * @return percent
     */
    @Deprecated
    @SuppressLint("WrongConstant")
    public static int getUnCacheProgress(Context context, String uniqueKey, long fileSize) {
        int percent = -1;
        SharedPreferences preferences = context.getSharedPreferences("DownLoadInfo", Context.MODE_APPEND);
        long total = preferences.getLong(uniqueKey, 0);
        if (total > 0) {
            percent = (int) (fileSize * 100 / total);
        } else {
            percent = 0;
        }
        return percent;
    }


    /**
     * @param downloadInfo 下载信息
     * @return 是否安装
     */
    @Deprecated
    public static boolean isInstalled(Context context, DownloadInfo downloadInfo) {//,String packageName
        try {
            PackageManager packageManager = context.getPackageManager();
            List<PackageInfo> packages = packageManager.getInstalledPackages(0);
            if (packages != null) {
                for (int i = 0; i < packages.size(); i++) {
                    PackageInfo packageInfo = packages.get(i);
                    if (packageInfo != null && packageInfo.packageName.equals(downloadInfo.getUniqueKey())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    //end region
}

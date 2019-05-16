package com.smasher.downloader.path;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;

/**
 * @author moyu
 * @date 2017/3/13
 */
public class AppPath {
    private static final String TAG = "AppPath";
    private static String RootPath;

    public static void init(String rootPath) {
        RootPath = rootPath;
    }

    private static String getRootPath(@NonNull Context context) {
        if (TextUtils.isEmpty(RootPath)) {
            Log.e(TAG, "getRootPath: ");
            return null;
        }

        String uniqueName = "/" + RootPath + "/";
        File path = context.getFilesDir();
        String result = (path == null ? "/data/data/" + context.getPackageName() + "/files" : path.getAbsolutePath()) + uniqueName;
        try {
            String state = Environment.getExternalStorageState();
            if (!TextUtils.isEmpty(state) && Environment.MEDIA_MOUNTED.equals(state)) {
                File externalPath = Environment.getExternalStorageDirectory();
                if (externalPath != null && externalPath.exists() && externalPath.canWrite()) {
                    result = externalPath.getAbsolutePath() + uniqueName;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        File dir = new File(result);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return result;
    }

    private static String getSubPath(Context context, String subName) {
        String result = getRootPath(context) + "/" + subName;
        File dir = new File(result);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getAbsolutePath() + "/";
    }

    public static String getCachePath(Context context) {
        return getSubPath(context, "cache");
    }

    public static String getDownloadPath(Context context) {
        return getSubPath(context, "download");
    }

    public static String getLogPath(Context context) {
        return getSubPath(context, "log");
    }

    public static String getImagePath(Context context) {
        return getSubPath(context, "image");
    }

    public static String getFontsPath(Context context) {
        return getSubPath(context, "fonts");
    }

}

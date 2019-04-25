package com.smasher.downloader.path;

import android.app.Application;
import android.os.Environment;
import android.support.annotation.NonNull;
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

    static String getRootPath(@NonNull Application application) {

        if (TextUtils.isEmpty(RootPath)) {
            Log.e(TAG, "getRootPath: ");
            return null;
        }

        String uniqueName = "/" + RootPath + "/";
        File path = application.getFilesDir();
        String result = (path == null ? "/data/data/" + application.getPackageName() + "/files" : path.getAbsolutePath()) + uniqueName;
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

    static String getSubPath(Application application, String subName) {
        String result = getRootPath(application) + "/" + subName;
        File dir = new File(result);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getAbsolutePath() + "/";
    }

    public static String getCachePath(Application application) {
        return getSubPath(application, "cache");
    }

    public static String getDownloadPath(Application application) {
        return getSubPath(application, "download");
    }

    public static String getLogPath(Application application) {
        return getSubPath(application, "log");
    }

    public static String getImagePath(Application application) {
        return getSubPath(application, "image");
    }

    public static String getFontsPath(Application application) {
        return getSubPath(application, "fonts");
    }

}

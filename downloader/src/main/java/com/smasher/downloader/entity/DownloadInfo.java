package com.smasher.downloader.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 下载信息
 *
 * @author matao
 */
public class DownloadInfo implements Parcelable {

    /**
     * 任务id
     */
    private int id;

    /**
     * 文件名(通知栏显示名称)
     */
    private String name;


    /**
     * 文件名-全名带后缀
     */
    private String fullName;

    /**
     * 下载地址
     */
    private String url;

    /**
     * 通知栏icon的下载地址
     */
    private String iconUrl;


    /**
     * 通知栏点击跳转地址url
     */
    private String actionUrl;

    /**
     * 文件长度
     */
    private long total;

    /**
     * 已下载文件长度
     */
    private long progress;


    /**
     * 存储key（真实唯一包名）
     */
    private String uniqueKey;

    /**
     * 保存地址
     */
    private String targetPath;


    /**
     * 下载类型
     */
    @DownloadType
    private int downLoadType = 0;


    /**
     * 状态信息
     */
    @State
    private int status = 0;


    private static final String SUFFIX = ".apk";


    public DownloadInfo() {
    }


    public DownloadInfo(String url) {
        this.url = url;
    }

    public DownloadInfo(String name, String fullName, String url, String iconUrl, String actionUrl, String uniqueKey) {
        this.name = name;
        this.fullName = fullName;
        this.url = url;
        this.iconUrl = iconUrl;
        this.actionUrl = actionUrl;
        this.uniqueKey = uniqueKey;
    }


    public void createFileWithUniqueKey() {
        if (!TextUtils.isEmpty(fullName)) {
            return;
        }
        if (uniqueKey != null && !TextUtils.isEmpty(uniqueKey)) {
            fullName = uniqueKey + SUFFIX;
        }
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }


    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getIconUrl() {
        return iconUrl;
    }


    public String getUniqueKey() {
        return uniqueKey;
    }


    /**
     * 若为下载apk  这里传入包名
     *
     * @param uniqueKey uniqueKey
     */
    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }


    public String getActionUrl() {
        return actionUrl;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }


    public int getStatus() {
        return status;
    }


    public void setStatus(int status) {
        this.status = status;
    }


    public int getDownLoadType() {
        return downLoadType;
    }

    public void setDownLoadType(int downLoadType) {
        this.downLoadType = downLoadType;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.fullName);
        dest.writeString(this.url);
        dest.writeString(this.iconUrl);
        dest.writeString(this.actionUrl);
        dest.writeLong(this.total);
        dest.writeLong(this.progress);
        dest.writeString(this.uniqueKey);
        dest.writeString(this.targetPath);
        dest.writeInt(this.downLoadType);
        dest.writeInt(this.status);
    }

    protected DownloadInfo(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.fullName = in.readString();
        this.url = in.readString();
        this.iconUrl = in.readString();
        this.actionUrl = in.readString();
        this.total = in.readLong();
        this.progress = in.readLong();
        this.uniqueKey = in.readString();
        this.targetPath = in.readString();
        this.downLoadType = in.readInt();
        this.status = in.readInt();
    }

    public static final Creator<DownloadInfo> CREATOR = new Creator<DownloadInfo>() {
        @Override
        public DownloadInfo createFromParcel(Parcel source) {
            return new DownloadInfo(source);
        }

        @Override
        public DownloadInfo[] newArray(int size) {
            return new DownloadInfo[size];
        }
    };


    @Override
    public boolean equals(Object obj) {
        return obj instanceof DownloadInfo && this.getUniqueKey().equals(((DownloadInfo) obj).getUniqueKey());
    }


    //region 下载类型

    /**
     * 普通文件
     */
    public static final int DOWN_LOAD_TYPE_COMMON = 100;

    /**
     * apk安装包文件
     */
    public static final int DOWN_LOAD_TYPE_APK = 101;

    @IntDef({DOWN_LOAD_TYPE_COMMON, DOWN_LOAD_TYPE_APK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DownloadType {
    }

    //region


    //region #状态

    /**
     * 默认状态
     */
    public static final int JS_STATE_NORMAL = 0;


    /**
     * 任务排队
     */
    public static final int JS_STATE_WAIT = 1;


    /**
     * 下载准备中
     */
    public static final int JS_STATE_DOWNLOAD_PRE = 2;


    /**
     * 下载准备中
     */
    public static final int JS_STATE_GET_TOTAL = 3;


    /**
     * 下载中
     */
    public static final int JS_STATE_DOWNLOADING = 4;


    /**
     * 暂停
     */
    public static final int JS_STATE_PAUSE = 5;


    /**
     * 下载完成
     */
    public static final int JS_STATE_FINISH = 6;


    /**
     * 失败
     */
    public static final int JS_STATE_FAILED = 7;


    /**
     * 已安装
     */
    public static final int JS_STATE_INSTALLED = 8;


    @IntDef({JS_STATE_NORMAL, JS_STATE_WAIT, JS_STATE_DOWNLOAD_PRE, JS_STATE_GET_TOTAL,
            JS_STATE_DOWNLOADING, JS_STATE_FINISH, JS_STATE_PAUSE, JS_STATE_FAILED,
            JS_STATE_INSTALLED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }

    // end region


}

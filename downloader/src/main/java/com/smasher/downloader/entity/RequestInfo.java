package com.smasher.downloader.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author matao
 * @date 2019/4/24
 */
public class RequestInfo implements Parcelable {


    public static final int COMMAND_DOWNLOAD = 0;
    public static final int COMMAND_PAUSE = 1;
    public static final int COMMAND_INSTALL = 2;
    public static final int COMMAND_OPEN = 3;


    @IntDef({COMMAND_DOWNLOAD, COMMAND_PAUSE, COMMAND_INSTALL, COMMAND_OPEN})
    @Retention(RetentionPolicy.SOURCE)
    @interface RequestType {
    }


    public RequestInfo() {
    }


    @RequestType
    private int mCommand = 0;

    private DownloadInfo mDownloadInfo;


    public int getCommand() {
        return mCommand;
    }

    public void setCommand(int command) {
        mCommand = command;
    }

    public DownloadInfo getDownloadInfo() {
        return mDownloadInfo;
    }

    public void setDownloadInfo(DownloadInfo downloadInfo) {
        mDownloadInfo = downloadInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mCommand);
        dest.writeParcelable(this.mDownloadInfo, flags);
    }


    protected RequestInfo(Parcel in) {
        this.mCommand = in.readInt();
        this.mDownloadInfo = in.readParcelable(DownloadInfo.class.getClassLoader());
    }

    public static final Creator<RequestInfo> CREATOR = new Creator<RequestInfo>() {
        @Override
        public RequestInfo createFromParcel(Parcel source) {
            return new RequestInfo(source);
        }

        @Override
        public RequestInfo[] newArray(int size) {
            return new RequestInfo[size];
        }
    };
}

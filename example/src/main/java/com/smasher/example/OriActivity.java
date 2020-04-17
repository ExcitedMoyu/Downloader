package com.smasher.example;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OriActivity extends AppCompatActivity {

    private static final String TAG = "OriActivity";
    @BindView(R.id.btn_download)
    Button mBtnDownload;
    @BindView(R.id.btn_query)
    Button mBtnQuery;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;


    public static final String MIME_APK = "application/vnd.android.package-archive";

    long id = -1;

    private DownloadManager mDownloadManager;

    public static final int HANDLE_DOWNLOAD = 0x001;

    private DownloadChangeObserver downloadObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ori);
        ButterKnife.bind(this);

        mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
    }

    private void registerObserver() {
        if (downloadObserver != null) {
            getContentResolver().registerContentObserver(
                    Uri.parse("content://downloads/all_downloads/" + id), false, downloadObserver);
            Log.d(TAG, "registerObserver: " + id);
        }
    }


    private void systemDownload() {
        Uri uri = Uri.parse("http://download.qidian.com/apknew/source/QDReader.apk");
        Request request = new Request(uri);
        request.setTitle("测试下载");
        request.setDescription("測試下在描述");
        request.allowScanningByMediaScanner();
        request.setMimeType(MIME_APK);
        request.setAllowedNetworkTypes(Request.NETWORK_MOBILE | Request.NETWORK_WIFI);
        request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(true);

//        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "mobileqq_android.apk");
//        Log.d(TAG, "systemDownload: " + file.getPath());
//        request.setDestinationUri(Uri.fromFile(file));

        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "mobileqq_android.apk");
        //request.setDestinationInExternalPublicDir("Download", "mobileqq_android.apk");
        //request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "QDReader.apk");
        id = mDownloadManager.enqueue(request);
        Log.d(TAG, "systemDownload: " + id);

        downloadObserver = new DownloadChangeObserver(downLoadHandler, Long.valueOf(id).intValue(), mDownloadManager);

        registerObserver();
    }


    private void query() {
        if (mDownloadManager != null) {

            Query query = new Query();
            query.setFilterById(id);

            Cursor cursor = null;
            try {
                cursor = mDownloadManager.query(query);
                if (cursor != null && cursor.moveToFirst()) {
                    //已经下载文件大小
                    int size = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    Log.d(TAG, "query: size-" + size);
                    //下载文件的总大小
                    int total = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    Log.d(TAG, "query: total-" + total);
                    //下载状态
                    int state = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    Log.d(TAG, "query: state-" + state);
                    switch (state) {
                        case DownloadManager.STATUS_FAILED:
                            Log.d(TAG, "query: state- 失敗");
                            break;
                        case DownloadManager.STATUS_SUCCESSFUL:
                            Log.d(TAG, "query: state- 成功");
                            break;
                        case DownloadManager.STATUS_PENDING:
                            Log.d(TAG, "query: state- 等待");
                            break;
                        case DownloadManager.STATUS_PAUSED:
                            Log.d(TAG, "query: state- 暂停");
                            break;
                        case DownloadManager.STATUS_RUNNING:
                            Log.d(TAG, "query: state- 下载中");
                            break;
                        default:
                            break;
                    }

                    Uri uri = mDownloadManager.getUriForDownloadedFile(id);
                    Log.d(TAG, "query: " + uri);
                } else {
                    Log.d(TAG, "query: cursor is null");
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }


    @OnClick({R.id.btn_download, R.id.btn_query})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_download:
                systemDownload();
                break;
            case R.id.btn_query:
                query();
                break;
            default:
                break;
        }
    }


    /**
     * 注销ContentObserver
     */
    private void unregisterContentObserver() {
        if (downloadObserver != null) {
            downloadObserver.close();
            getContentResolver().unregisterContentObserver(downloadObserver);
        }
    }

    /**
     * 关闭定时器，线程等操作
     */
    private void close() {
        if (downLoadHandler != null) {
            downLoadHandler.removeCallbacksAndMessages(null);
        }
    }


    @SuppressLint("HandlerLeak")
    public Handler downLoadHandler = new Handler() {
        //主线程的handler
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (HANDLE_DOWNLOAD == msg.what) {
                //被除数可以为0，除数必须大于0
                ResultInfo info = (ResultInfo) msg.obj;
                long size = info.getSize();
                long total = info.getTotal();
                int progress = (int) (size * 100 / total);

                Log.d(TAG, "handleMessage: progress-" + progress);
                mProgressBar.setProgress(progress);
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        close();
        unregisterContentObserver();
    }
}

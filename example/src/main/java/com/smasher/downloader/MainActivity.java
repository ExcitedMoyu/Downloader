package com.smasher.downloader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.smasher.downloader.entity.DownloadInfo;
import com.smasher.downloader.manager.DownloadManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author moyu
 */
public class MainActivity extends AppCompatActivity {


    @BindView(R.id.button)
    Button button;


    /**
     * 豌豆荚 下载地址
     */
    private static final String FIRST_URL = "http://ucan.25pp.com/Wandoujia_web_seo_baidu_homepage.apk";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button)
    public void onViewClicked() {
        String firstName = "豌豆荚.apk";

        DownloadInfo info = new DownloadInfo();
        info.setId(100001);
        info.setDownLoadType(DownloadInfo.DOWN_LOAD_TYPE_COMMON);
        info.setFullName(firstName);
        info.setUrl(FIRST_URL);
        info.setUniqueKey("");
        info.setName("豌豆荚");
        DownloadManager.getInstance().addTask(info).submit(this);
    }
}

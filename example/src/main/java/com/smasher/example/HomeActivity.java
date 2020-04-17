package com.smasher.example;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author matao
 */
public class HomeActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    @BindView(R.id.btn_permission)
    Button btnPermission;
    @BindView(R.id.btn_define)
    Button btnDefine;
    @BindView(R.id.btn_origin)
    Button btnOrigin;

    String PERMISSION_STORAGE_MSG = "请授予权限，否则影响部分使用功能";
    public static final int PERMISSION_STORAGE_CODE = 10001;
    String[] PERMS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
    }


    private void requestPs() {
        if (EasyPermissions.hasPermissions(this, PERMS)) {
            // 已经申请过权限，做想做的事
            onPermissionSuccess();
        } else {
            // 没有申请过权限，现在去申请
            /**
             *@param host Context对象
             *@param rationale  权限弹窗上的提示语。
             *@param requestCode 请求权限的唯一标识码
             *@param perms 一系列权限
             */
            EasyPermissions.requestPermissions(this, PERMISSION_STORAGE_MSG, PERMISSION_STORAGE_CODE, PERMS);
        }

    }


    @AfterPermissionGranted(PERMISSION_STORAGE_CODE)
    public void onPermissionSuccess() {
        Toast.makeText(this, "AfterPermission调用成功了", Toast.LENGTH_SHORT).show();
    }


    @OnClick({R.id.btn_permission, R.id.btn_define, R.id.btn_origin})
    public void onViewClicked(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btn_permission:
                requestPs();
                break;
            case R.id.btn_define:
                intent = new Intent();
                intent.setClass(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_origin:
                intent = new Intent();
                intent.setClass(this, OriActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        /**
         * 若是在权限弹窗中，用户勾选了'不在提示'，且拒绝权限。
         * 这时候，需要跳转到设置界面去，让用户手动开启。
         */
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            //从设置页面返回，判断权限是否申请。
            if (EasyPermissions.hasPermissions(this, PERMS)) {
                Toast.makeText(this, "权限申请成功!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "权限申请失败!", Toast.LENGTH_SHORT).show();

            }
        }
    }
}

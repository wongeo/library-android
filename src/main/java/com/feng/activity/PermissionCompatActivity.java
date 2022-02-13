package com.feng.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import com.feng.mvp.BaseActivity;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

/**
 * Created by wj148202 on 2017/11/3.
 */

public class PermissionCompatActivity extends BaseActivity {

    public static final int PERMISSION_REQUEST = 2000;
    public static final int RUEQUEST_CODE_SETTING = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestAllPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                boolean isSecondRequest = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]);
                if (isSecondRequest) {
                    /**重新请求授予权限，显示权限说明（该说明属于系统UI内容，区别第一次弹窗）**/
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST);
                } else {
                    showSettingDialog();
                }
            }
        }
    }

    private void showSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permission:");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName())); // 根据包名打开对应的设置界面
                startActivity(intent);
            }
        });
        builder.create().show();
    }

    public void requestAllPermissions() {
        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) == PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, permissions, PermissionCompatActivity.PERMISSION_REQUEST);
                return;
            }
        }
    }
}

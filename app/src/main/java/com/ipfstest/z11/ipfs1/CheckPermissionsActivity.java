package com.ipfstest.z11.ipfs1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.List;

public class CheckPermissionsActivity extends AppCompatActivity {

    private static final int PERMISSON_REQUESTCODE = 0;
    private static final int REQUEST_CODE_WRITE_SETTINGS = 1;
    private static final int REQUEST_CODE_UNKNOWN_APP_SOURCES = 2;
    protected boolean hasPermission = true;


    public boolean isNeedCheck = true;

    private List<String> getNeedPermissions() {
        List<String> list = new ArrayList<>();
        list.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        return list;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isNeedCheck){
            checkPermissions(getNeedPermissions());
        }
    }

    private void checkPermissions(List<String> permissions) {
        if (permissions.remove(Manifest.permission.WRITE_SETTINGS) &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                showRequestWriteSettingDialog();
                return;
            }
        }
        if (permissions.remove(Manifest.permission.REQUEST_INSTALL_PACKAGES) &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!getPackageManager().canRequestPackageInstalls()) {
                showRequestUnknownAppSourceDialog();
                return;
            }
        }
        List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (needRequestPermissonList.size() > 0) {
            ActivityCompat.requestPermissions(this, needRequestPermissonList.toArray(new String[0]), PERMISSON_REQUESTCODE);
        } else {
            isNeedCheck = false;
            onPermissionsRequestResult(true);
        }
    }

    //获取权限集中需要申请权限的列表
    private List<String> findDeniedPermissions(List<String> permissions) {
        List<String> needRequestPermissonList = new ArrayList<>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                needRequestPermissonList.add(perm);
            }
        }
        return needRequestPermissonList;
    }

    //检测是否所有的权限都已经授权
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void showRequestWriteSettingDialog() {
        showDialog(getString(R.string.request_permission), getString(R.string.need_write_setting), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    startActivityForResult(new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS), REQUEST_CODE_WRITE_SETTINGS);
                }
            }
        });
    }

    private void showRequestUnknownAppSourceDialog() {
        showDialog(getString(R.string.request_permission), getString(R.string.need_unknown_app_source), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES), REQUEST_CODE_UNKNOWN_APP_SOURCES);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] paramArrayOfInt) {
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(paramArrayOfInt)) {
                hasPermission = false;
            }
            isNeedCheck = false;
            onPermissionsRequestResult(hasPermission);
            if (!hasPermission) {
                showMissingPermissionDialog();
            }
        }
    }

    /**
     * 是否已获得所有申请的权限
     */
    public boolean isHasPermission() {
        return hasPermission;
    }

    /**
     * 权限申请结果
     * @param hasPermission true已获取所有申请的权限,false未全部获得
     */
    protected void onPermissionsRequestResult(boolean hasPermission) {}

    /**
     * 显示提示信息
     */
    private void showMissingPermissionDialog() {
        showDialog(getString(R.string.request_permission), getString(R.string.lack_permission_msg), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isNeedCheck = true;
                //启动应用的设置
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        });
    }

    private void showDialog(String title, String msg, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .setPositiveButton(R.string.setting, listener)
                .show();
    }
}

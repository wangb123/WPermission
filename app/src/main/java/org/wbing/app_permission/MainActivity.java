package org.wbing.app_permission;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.wbing.app_permission.databinding.ActivityMainBinding;
import org.wbing.base.ui.impl.WAct;
import org.wbing.permission.WPermission;
import org.wbing.permission.WPermissionFailed;

public class MainActivity extends WAct<ActivityMainBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getBinding().getPermission.setOnClickListener(v -> getPermission());
    }

    @Override
    public int layoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void loadData() {

    }

    @Override
    public void recycle() {

    }

    /**
     *需要获取权限的界面添加WPermission，参数为需要申请的权限
     */
    @WPermission({Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void getPermission() {
        Log.e("TAG", "获取权限成功");
    }

    /**
     * 获取权限失败
     */
    @WPermissionFailed
    public void getPermissionFailed() {
        Log.e("TAG", "获取权限失败");
    }
}

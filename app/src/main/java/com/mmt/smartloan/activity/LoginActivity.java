package com.mmt.smartloan.activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import com.gyf.immersionbar.ImmersionBar;
import com.mmt.smartloan.BR;
import com.mmt.smartloan.R;
import com.mmt.smartloan.base.BaseActivity;
import com.mmt.smartloan.base.BaseApplication;
import com.mmt.smartloan.databinding.LoginLayoutBinding;
import com.mmt.smartloan.module.LoginModule;
import com.mmt.smartloan.repository.AppViewModelFactory;
import com.mmt.smartloan.utils.AdvertisingIdClient;
import com.mmt.smartloan.utils.InstallReferrerBroadcastReceiver;

import java.util.List;
import java.util.concurrent.Executors;

import pub.devrel.easypermissions.EasyPermissions;

public class LoginActivity extends BaseActivity<LoginLayoutBinding, LoginModule> {
    private String adid;
    String[] perms = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_SMS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    public int getLayout() {
        return R.layout.login_layout;
    }

    @Override
    public int initVariableId() {
        return BR.login;
    }

    @Override
    public LoginModule initViewModel(AppViewModelFactory factory) {
        return ViewModelProviders.of(this, factory).get(LoginModule.class);
    }

    @Override
    public void initView() {
        ImmersionBar.with(this).statusBarColor("#5D48BD").fitsSystemWindows(true).init();
        methodRequiresTwoPermission();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private int RC_CAMERA_AND_LOCATION = 0;

    private void methodRequiresTwoPermission() {
        if (EasyPermissions.hasPermissions(this, perms)) {
            // 已经有权限
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    adid = AdvertisingIdClient.getGoogleAdId(BaseApplication.getAppContext());
                    Log.e("LoginModule>>>>>>>", "adid:  " + adid);
                    viewModel.addActive(adid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });


        } else {
            // 没有权限，现在请求他们
            //只有用户首次安装时拒绝了权限，才会在下次申请时弹出 "此app需要xxx权限"提示框
            EasyPermissions.requestPermissions(this, "此app需要获取电话存储权限", RC_CAMERA_AND_LOCATION, perms);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

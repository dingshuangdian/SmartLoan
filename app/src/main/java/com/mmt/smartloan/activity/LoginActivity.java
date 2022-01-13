package com.mmt.smartloan.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import com.gyf.immersionbar.ImmersionBar;
import com.mmt.smartloan.BR;
import com.mmt.smartloan.R;
import com.mmt.smartloan.base.AddressConfig;
import com.mmt.smartloan.base.BaseActivity;
import com.mmt.smartloan.base.BaseApplication;
import com.mmt.smartloan.cache.BaseCacheManager;
import com.mmt.smartloan.databinding.LoginLayoutBinding;
import com.mmt.smartloan.module.LoginModule;
import com.mmt.smartloan.repository.AppViewModelFactory;
import com.mmt.smartloan.utils.AdvertisingIdClient;

import java.util.concurrent.Executors;

import pub.devrel.easypermissions.EasyPermissions;

public class LoginActivity extends BaseActivity<LoginLayoutBinding, LoginModule> {
    private String gaid;
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
        viewModel.getIpAddress();
        ImmersionBar.with(this).statusBarColor("#AD2648").fitsSystemWindows(true).init();
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                gaid = AdvertisingIdClient.getGoogleAdId(BaseApplication.getAppContext());
                BaseCacheManager.getUserTemp().setGaid(gaid);
                viewModel.getInstallReferrer(gaid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        String text = "Al continuar,acepta nuestros<Términos de Servicio>,\n" +
                "<Política de privacidad>y recibe avisos por SMS y\n" +
                "correo electrónico.";
        SpannableString spanStr = new SpannableString(text);

        String ysText = "<Términos de Servicio>";
        String xyText = "<Política de privacidad>";
        int firstIndex = text.indexOf(ysText);
        int secondIndex = text.indexOf(xyText);
        spanStr.setSpan(new MyURLSpan(Color.parseColor("#A11134")) {
            @Override
            public void onClick(View widget) {
                try {
                    Intent intent = new Intent(LoginActivity.this, ShowWebActivity.class);
                    intent.putExtra(ShowWebActivity.KEY_URL, AddressConfig.WEB_URL + "/#/provicy");
                    intent.putExtra(ShowWebActivity.KEY_TITLE, "Términos de Servicio");
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, firstIndex, firstIndex + ysText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spanStr.setSpan(new MyURLSpan(Color.parseColor("#A11134")) {
            @Override
            public void onClick(View widget) {
                try {
                    Intent intent = new Intent(LoginActivity.this, ShowWebActivity.class);
                    intent.putExtra(ShowWebActivity.KEY_URL, AddressConfig.WEB_URL + "/#/termsCondition");
                    intent.putExtra(ShowWebActivity.KEY_TITLE, "Política de privacidad");
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, secondIndex, secondIndex + xyText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        binding.tvYs.setMovementMethod(LinkMovementMethod.getInstance());
        binding.tvYs.setText(spanStr, TextView.BufferType.SPANNABLE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private int RC_CAMERA_AND_LOCATION = 0;


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class MyURLSpan extends ClickableSpan {
        private int colorId;

        public MyURLSpan(int colorId) {
            this.colorId = colorId;
        }

        @Override
        public void onClick(@NonNull View widget) {

        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(colorId);
            ds.setUnderlineText(false);
        }
    }
}

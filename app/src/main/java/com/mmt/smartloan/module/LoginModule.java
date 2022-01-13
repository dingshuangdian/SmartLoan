package com.mmt.smartloan.module;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LifecycleOwner;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.mmt.smartloan.R;
import com.mmt.smartloan.activity.VerifyActivity;
import com.mmt.smartloan.base.ActivityStackManager;
import com.mmt.smartloan.base.BaseApplication;
import com.mmt.smartloan.base.BaseParameter;
import com.mmt.smartloan.base.BaseViewModel;
import com.mmt.smartloan.bean.ExistsMobileBean;
import com.mmt.smartloan.bean.RegisterAndLoginBean;
import com.mmt.smartloan.bean.VerifyCodeBean;
import com.mmt.smartloan.cache.BaseCacheManager;
import com.mmt.smartloan.repository.RepositoryModule;
import com.mmt.smartloan.rxjava.exception.core.RxLifecycleUtils;
import com.mmt.smartloan.rxjava.network.MyObserver;
import com.mmt.smartloan.utils.DateTimeUtil;
import com.mmt.smartloan.utils.RxUtil;
import com.mmt.smartloan.utils.ToastUtils;
import com.mmt.smartloan.utils.device.DeviceUtils;
import com.mmt.smartloan.view.webview.ByWebViewActivity;

public class LoginModule extends BaseViewModel<RepositoryModule> {
    private String installReferce, installReferceClickTime, installStartTime;
    public ObservableField<String> phone = new ObservableField<>();
    private final char SEPARATOR = ' ';
    private String gaid;
    public ObservableField<Boolean> check = new ObservableField<>(true);

    public LoginModule(RepositoryModule model, Activity activity) {
        super(model, activity);

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                if (TextUtils.isEmpty(phone.get())) {
                    ToastUtils.showToast("请输入手机号");
                    return;
                }
                if (phone.get().replace(String.valueOf(SEPARATOR), "").length() < 10) {
                    ToastUtils.showToast("手机号格式不符合要求");
                    return;
                }
                if (!check.get()) {
                    ToastUtils.showToast("请勾选服务协议");
                    return;
                }
                model.existsByMobile(BaseParameter.existsByMobile(phone.get().replace(String.valueOf(SEPARATOR), "")))
                        .compose(RxUtil.getWrapper())
                        .as(RxLifecycleUtils.bindLifecycle((LifecycleOwner) activity))
                        .subscribe(new MyObserver<ExistsMobileBean>() {
                            @Override
                            public void onResultSuccess(ExistsMobileBean o) {
                                getVerifyCode(o);

                            }
                        });

                break;
            case R.id.cb:
                check.set(!check.get());
                break;

        }
    }

    private void getVerifyCode(ExistsMobileBean existsMobileBean) {
        model.getVerifyCode(BaseParameter.getVerifyCode(phone.get().replace(String.valueOf(SEPARATOR), ""), existsMobileBean.isExisted() ? 1 : 2, gaid))
                .compose(RxUtil.getWrapperWithLoading(activity))
                .as(RxLifecycleUtils.bindLifecycle((LifecycleOwner) activity))
                .subscribe(new MyObserver<VerifyCodeBean>() {
                    @Override
                    public void onResultSuccess(VerifyCodeBean o) {
                        if (o.isEnableAutoLogin() && existsMobileBean.isExisted()) {
                            loginSys(o.getCode());
                        } else {
                            Intent intent = new Intent(activity, VerifyActivity.class);
                            intent.putExtra("mobile", phone.get().replace(String.valueOf(SEPARATOR), ""));
                            intent.putExtra("isExisted", existsMobileBean.isExisted());
                            intent.putExtra("gaid", gaid);
                            activity.startActivity(intent);
                        }


                    }
                });
    }

    private void loginSys(String code) {
        model.loginSys(BaseParameter.loginSys(phone.get().replace(String.valueOf(SEPARATOR), ""), code, true))
                .compose(RxUtil.getWrapperWithLoading(activity))
                .as(RxLifecycleUtils.bindLifecycle((LifecycleOwner) activity))
                .subscribe(new MyObserver<RegisterAndLoginBean>() {
                    @Override
                    public void onResultSuccess(RegisterAndLoginBean o) {
                        BaseCacheManager.getUserTemp().setToken(o.getToken());
                        BaseCacheManager.getUserTemp().setUserId(o.getUserId());
                        ByWebViewActivity.loadUrl(activity, "http://8.134.38.88:3003/", "", 0);
                        ActivityStackManager.getInstance().finishAllActivity();
                    }
                });
    }

    public void addActive(String gaid) {
        model.setActivePush(BaseParameter.addActiveParams(gaid, installReferce, installReferceClickTime, installStartTime))
                .compose(RxUtil.getWrapper())
                .as(RxLifecycleUtils.bindLifecycle((LifecycleOwner) activity))
                .subscribe(new MyObserver<Object>() {
                    @Override
                    public void onResultSuccess(Object o) {

                    }
                });
    }

    public void getIpAddress() {
        new Thread(() -> BaseCacheManager.getUserTemp().setIpaddress(DeviceUtils.getOutNetIP(BaseApplication.getAppContext(), 0))).start();

    }


    public void getInstallReferrer(String gaid) {
        this.gaid = gaid;
        try {
            final InstallReferrerClient installReferrerClient = InstallReferrerClient.newBuilder(BaseApplication.getAppContext()).build();
            installReferrerClient.startConnection(new InstallReferrerStateListener() {
                @Override
                public void onInstallReferrerSetupFinished(int responseCode) {
                    switch (responseCode) {
                        case InstallReferrerClient.InstallReferrerResponse.OK:
                            // Connection established, get referrer
                            if (installReferrerClient != null) {
                                try {
                                    ReferrerDetails response = installReferrerClient.getInstallReferrer();
                                    installReferce = response.getInstallReferrer();// 你要得referrer值
                                    installReferceClickTime = DateTimeUtil.getFormatTime(response.getReferrerClickTimestampSeconds() * 1000, "yyyy-MM-dd kk:mm:ss");
                                    installStartTime = DateTimeUtil.getFormatTime(response.getInstallBeginTimestampSeconds() * 1000, "yyyy-MM-dd kk:mm:ss");
                                    if (!TextUtils.isEmpty(installReferce) && !TextUtils.isEmpty(BaseCacheManager.getUserTemp().getIpaddress())) {
                                        BaseCacheManager.getUserTemp().setINSTALLREFERCE(installReferce);
                                        BaseCacheManager.getUserTemp().setUtmSource(installReferce.split("=")[1].split("&")[0]);
                                        addActive(gaid);
                                    }
                                    installReferrerClient.endConnection();
                                } catch (Exception ex) {
                                    Log.e("InstallReferrerHelper", ex.toString());
                                }
                            }
                            break;
                        case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                            // API not available on the current Play Store app
                            Log.d("InstallReferrerHelper", "FEATURE_NOT_SUPPORTED");
                            break;
                        case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                            // Connection could not be established
                            Log.d("InstallReferrerHelper", "SERVICE_UNAVAILABLE");
                            break;
                    }
                }

                @Override
                public void onInstallReferrerServiceDisconnected() {
                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                }

            });
        } catch (Exception ex) {
            Log.e("InstallReferrerHelper", ex.toString());
        }
    }
}

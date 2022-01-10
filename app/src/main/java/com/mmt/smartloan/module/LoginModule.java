package com.mmt.smartloan.module;

import android.app.Activity;
import android.content.Intent;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.mmt.smartloan.MainActivity;
import com.mmt.smartloan.R;
import com.mmt.smartloan.base.BaseApplication;
import com.mmt.smartloan.base.BaseParameter;
import com.mmt.smartloan.base.BaseViewModel;
import com.mmt.smartloan.repository.RepositoryModule;
import com.mmt.smartloan.rxjava.exception.core.RxLifecycleUtils;
import com.mmt.smartloan.rxjava.network.MyObserver;
import com.mmt.smartloan.utils.AdvertisingIdClient;
import com.mmt.smartloan.utils.DateTimeUtil;
import com.mmt.smartloan.utils.LogUtils;
import com.mmt.smartloan.utils.RxUtil;

import java.util.concurrent.Executors;

public class LoginModule extends BaseViewModel<RepositoryModule> {
    private String adid;
    private String installReferce, installReferceClickTime, installStartTime;

    public LoginModule(RepositoryModule model, Activity activity) throws RemoteException {
        super(model, activity);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                adid = AdvertisingIdClient.getGoogleAdId(BaseApplication.getAppContext());
                Log.e("LoginModule>>>>>>>", "adid:  " + adid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        getInstallReferrer();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);

        }
    }

    public void addActive() {
        model.addActive(BaseParameter.addActive(adid, installReferce, installReferceClickTime, installStartTime))
                .compose(RxUtil.getWrapper())
                .as(RxLifecycleUtils.bindLifecycle((LifecycleOwner) activity))
                .subscribe(new MyObserver<Object>() {
                    @Override
                    public void onResultSuccess(Object o) {

                    }
                });
    }

    private void getInstallReferrer() {
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
                                    if (!TextUtils.isEmpty(installReferce)) {
                                        addActive();
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

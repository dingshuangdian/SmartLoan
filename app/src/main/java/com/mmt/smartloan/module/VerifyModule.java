package com.mmt.smartloan.module;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LifecycleOwner;

import com.mmt.smartloan.R;
import com.mmt.smartloan.activity.VerifyActivity;
import com.mmt.smartloan.base.ActivityStackManager;
import com.mmt.smartloan.base.AddressConfig;
import com.mmt.smartloan.base.BaseParameter;
import com.mmt.smartloan.base.BaseViewModel;
import com.mmt.smartloan.bean.RegisterAndLoginBean;
import com.mmt.smartloan.bean.VerifyCodeBean;
import com.mmt.smartloan.cache.BaseCacheManager;
import com.mmt.smartloan.repository.RepositoryModule;
import com.mmt.smartloan.rxjava.exception.core.RxLifecycleUtils;
import com.mmt.smartloan.rxjava.network.MyObserver;
import com.mmt.smartloan.utils.RxUtil;
import com.mmt.smartloan.utils.ToastUtils;
import com.mmt.smartloan.view.webview.ByWebViewActivity;

/**
 * <p>版权©️所有：大参林医药集团<p>
 * <p>作者：dingshuangdian<p>
 * <p>创建时间：2022/1/11<p>
 */
public class VerifyModule extends BaseViewModel<RepositoryModule> {
    private VerifyActivity verifyActivity;
    public ObservableField<Boolean> check = new ObservableField<>(true);
    public ObservableField<String> code = new ObservableField<>();

    public VerifyModule(RepositoryModule model, Activity activity) {
        super(model, activity);
        verifyActivity = (VerifyActivity) activity;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_01:
                verifyActivity.mCountDownTimer.start();
                getVerifyCode();
                break;
            case R.id.login:
                if (TextUtils.isEmpty(code.get())) {
                    ToastUtils.showToast("请输入验证码");
                    return;
                }
                if (code.get().length() < 6) {
                    ToastUtils.showToast("验证码格式不符合要求");
                    return;
                }
                if (!check.get()) {
                    ToastUtils.showToast("请勾选服务协议");
                    return;
                }

                if (verifyActivity.isExisted) {
                    loginSys();
                } else {
                    registerSys();
                }

                break;
            case R.id.cb:
                check.set(!check.get());
                break;
        }

    }

    private void getVerifyCode() {
        model.getVerifyCode(BaseParameter.getVerifyCode(verifyActivity.mobile, verifyActivity.isExisted ? 1 : 2, verifyActivity.gaid))
                .compose(RxUtil.getWrapper())
                .as(RxLifecycleUtils.bindLifecycle((LifecycleOwner) activity))
                .subscribe(new MyObserver<VerifyCodeBean>() {
                    @Override
                    public void onResultSuccess(VerifyCodeBean o) {

                    }
                });
    }

    private void loginSys() {
        model.loginSys(BaseParameter.loginSys(verifyActivity.mobile, code.get(), false))
                .compose(RxUtil.getWrapperWithLoading(verifyActivity))
                .as(RxLifecycleUtils.bindLifecycle((LifecycleOwner) activity))
                .subscribe(new MyObserver<RegisterAndLoginBean>() {
                    @Override
                    public void onResultSuccess(RegisterAndLoginBean o) {
                        BaseCacheManager.getUserTemp().setToken(o.getToken());
                        BaseCacheManager.getUserTemp().setUserId(o.getUserId());
                        BaseCacheManager.getUserTemp().setPhone(verifyActivity.mobile);
                        ByWebViewActivity.loadUrl(activity, AddressConfig.WEB_URL, "", 0);
                        ActivityStackManager.getInstance().finishAllActivity();
                    }
                });
    }

    private void registerSys() {
        model.registerSys(BaseParameter.registerSys(verifyActivity.mobile, code.get()))
                .compose(RxUtil.getWrapperWithLoading(verifyActivity))
                .as(RxLifecycleUtils.bindLifecycle((LifecycleOwner) activity))
                .subscribe(new MyObserver<RegisterAndLoginBean>() {
                    @Override
                    public void onResultSuccess(RegisterAndLoginBean o) {
                        BaseCacheManager.getUserTemp().setToken(o.getToken());
                        BaseCacheManager.getUserTemp().setUserId(o.getUserId());
                        BaseCacheManager.getUserTemp().setPhone(verifyActivity.mobile);
                        ByWebViewActivity.loadUrl(activity, AddressConfig.WEB_URL, "", 0);
                        ActivityStackManager.getInstance().finishAllActivity();
                    }
                });
    }

}

package com.mmt.smartloan.module;

import android.app.Activity;

import androidx.lifecycle.LifecycleOwner;

import com.mmt.smartloan.base.BaseViewModel;
import com.mmt.smartloan.bean.RegisterAndLoginBean;
import com.mmt.smartloan.cache.BaseCacheManager;
import com.mmt.smartloan.repository.RepositoryModule;
import com.mmt.smartloan.rxjava.exception.core.RxLifecycleUtils;
import com.mmt.smartloan.rxjava.network.MyObserver;
import com.mmt.smartloan.utils.RxUtil;
import com.mmt.smartloan.view.webview.ByWebViewActivity;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * <p>版权©️所有：大参林医药集团<p>
 * <p>作者：dingshuangdian<p>
 * <p>创建时间：2022/1/12<p>
 */
public class WebViewModule extends BaseViewModel<RepositoryModule> {
    private ByWebViewActivity webViewActivity;

    public WebViewModule(RepositoryModule model, Activity activity) {
        super(model, activity);
        webViewActivity = (ByWebViewActivity) activity;
    }

    public void up6In1(File file, String md5, String orderNo, boolean isSubmit, int num) {
        RequestBody paramMd5 = RequestBody.create(
                MediaType.parse("multipart/form-data"), md5);
        RequestBody paramOrderNo = RequestBody.create(
                MediaType.parse("multipart/form-data"), orderNo);
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("file"), file);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        up6zip(paramMd5, paramOrderNo, filePart, isSubmit, num);

    }

    private void up6zip(RequestBody paramMd5, RequestBody paramOrderNo, MultipartBody.Part filePart, boolean isSubmit, int num) {
        int n = num + 1;
        model.zip6in1(paramMd5, paramOrderNo, filePart).compose(RxUtil.getWrapper())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<Object>() {
                    @Override
                    public void onResultSuccess(Object o) {
                        if ((Boolean) o) {
                            webViewActivity.timeManager.toJsResult(true);
                        } else {
                            if (isSubmit) {
                                if (n <= 3) {
                                    up6zip(paramMd5, paramOrderNo, filePart, isSubmit, n);
                                } else {
                                    webViewActivity.timeManager.toJsResult(false);
                                }
                            } else {
                                webViewActivity.timeManager.toJsResult(false);
                            }
                        }
                    }
                });
    }
}

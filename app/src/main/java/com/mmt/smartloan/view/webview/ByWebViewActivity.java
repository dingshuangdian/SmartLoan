package com.mmt.smartloan.view.webview;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import com.gyf.immersionbar.ImmersionBar;
import com.happy.dce.OnTimeFileCallBack;
import com.happy.dce.TimeManager;
import com.mmt.smartloan.BR;
import com.mmt.smartloan.BuildConfig;
import com.mmt.smartloan.R;
import com.mmt.smartloan.activity.LoginActivity;
import com.mmt.smartloan.base.BaseActivity;
import com.mmt.smartloan.base.BaseApplication;
import com.mmt.smartloan.cache.BaseCacheManager;
import com.mmt.smartloan.databinding.ActivityByWebviewBinding;
import com.mmt.smartloan.event.EventCommand;
import com.mmt.smartloan.module.WebViewModule;
import com.mmt.smartloan.repository.AppViewModelFactory;
import com.mmt.smartloan.utils.LogUtils;
import com.mmt.smartloan.utils.device.DeviceUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import ai.advance.liveness.lib.GuardianLivenessDetectionSDK;
import ai.advance.liveness.lib.LivenessResult;
import ai.advance.liveness.sdk.activity.LivenessActivity;
import pub.devrel.easypermissions.EasyPermissions;

public class ByWebViewActivity extends BaseActivity<ActivityByWebviewBinding, WebViewModule> {
    public final int PHONE_REQUEST_PERMISSION = 8848;
    public static final int REQUEST_CODE_LIVENESS = 8847;
    public final int PICK_CONTACT = 7747;

    String[] permsPhone = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA};
    // 网页链接
    private int mState;
    private String mUrl, phoneName, phoneNumber;
    private ByWebView byWebView;
    public TimeManager timeManager;
    private boolean logout;


    @Override
    public int getLayout() {
        return R.layout.activity_by_webview;
    }

    @Override
    public int initVariableId() {
        return BR.webView;
    }

    @Override
    public WebViewModule initViewModel(AppViewModelFactory factory) {
        return ViewModelProviders.of(this, factory).get(WebViewModule.class);
    }

    @Override
    public void initView() {
        ImmersionBar.with(this).statusBarColor("#5D48BD").fitsSystemWindows(true).init();
        getIntentData();
        initTitle();
        getDataFromBrowser(getIntent());

    }

    private void getIntentData() {
        mUrl = getIntent().getStringExtra("url");
        mState = getIntent().getIntExtra("state", 0);
    }


    private void initTitle() {
        LinearLayout container = findViewById(R.id.ll_container);
        byWebView = ByWebView
                .with(this)
                .setWebParent(container, new LinearLayout.LayoutParams(-1, -1))
                .useWebProgress(ContextCompat.getColor(this, R.color.coloRed))
                .setOnByWebClientCallback(onByWebClientCallback)
                .loadUrl(mUrl);
        timeManager = new TimeManager(this, byWebView.getWebView(), new OnTimeFileCallBack() {
            @Override
            public void onFile(File file, String s, String s1, boolean b) {
                viewModel.up6In1(file, s, s1, b, 0);
            }
        });
        byWebView.getWebView().addJavascriptInterface(new MyJavascriptInterface(), "FKSDKJsFramework");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == timeManager.TIME_REQUEST_PERMISSION) {
            timeManager.onRequestPermission();
        }
        if (requestCode == PHONE_REQUEST_PERMISSION) {
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        }
    }

    private OnByWebClientCallback onByWebClientCallback = new OnByWebClientCallback() {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.e("---onPageStarted", url);
        }

        @Override
        public boolean onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            // 如果自己处理，需要返回true
            return super.onReceivedSslError(view, handler, error);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // 网页加载完成后的回调
            if (mState == 1) {
                loadImageClickJs();
                loadTextClickJs();
                loadWebsiteSourceCodeJs();
            } else if (mState == 2) {
                loadCallJs();
            }
        }

        @Override
        public boolean isOpenThirdApp(String url) {
            // 处理三方链接
            Log.e("---url", url);
            return ByWebTools.handleThirdApp(ByWebViewActivity.this, url);
        }
    };


    /**
     * 前端注入JS：
     * 这段js函数的功能就是，遍历所有的img节点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
     */
    private void loadImageClickJs() {
        byWebView.getLoadJsHolder().loadJs("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\");" +
                "for(var i=0;i<objs.length;i++)" +
                "{" +
                "objs[i].onclick=function(){window.injectedObject.imageClick(this.getAttribute(\"src\"));}" +
                "}" +
                "})()");
    }

    /**
     * 前端注入JS：
     * 遍历所有的<li>节点,将节点里的属性传递过去(属性自定义,用于页面跳转)
     */
    private void loadTextClickJs() {
        byWebView.getLoadJsHolder().loadJs("javascript:(function(){" +
                "var objs =document.getElementsByTagName(\"li\");" +
                "for(var i=0;i<objs.length;i++)" +
                "{" +
                "objs[i].onclick=function(){" +
                "window.injectedObject.textClick(this.getAttribute(\"type\"),this.getAttribute(\"item_pk\"));}" +
                "}" +
                "})()");
    }

    /**
     * 传应用内的数据给html，方便html处理
     */
    private void loadCallJs() {
        // 无参数调用
        byWebView.getLoadJsHolder().quickCallJs("javacalljs");
        // 传递参数调用
        byWebView.getLoadJsHolder().quickCallJs("javacalljswithargs", "android传入到网页里的数据，有参");
    }

    /**
     * get website source code
     * 获取网页源码
     */
    private void loadWebsiteSourceCodeJs() {
        byWebView.getLoadJsHolder().loadJs("javascript:window.injectedObject.showSource(document.getElementsByTagName('html')[0].innerHTML);");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == PICK_CONTACT) {
            getContacts(intent);
        } else if (requestCode == REQUEST_CODE_LIVENESS) {
            if (LivenessResult.isSuccess()) {// 活体检测成功
                String livenessId = LivenessResult.getLivenessId();// 本次活体id
                Bitmap livenessBitmap = LivenessResult.getLivenessBitmap();// 本次活体图片
            } else {// 活体检测失败
                String errorCode = LivenessResult.getErrorCode();// 失败错误码
                String errorMsg = LivenessResult.getErrorMsg();// 失败原因
            }
        } else {
            byWebView.handleFileChooser(requestCode, resultCode, intent);
        }

    }

    /**
     * 使用singleTask启动模式的Activity在系统中只会存在一个实例。
     * 如果这个实例已经存在，intent就会通过onNewIntent传递到这个Activity。
     * 否则新的Activity实例被创建。
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getDataFromBrowser(intent);
    }

    /**
     * 作为三方浏览器打开传过来的值
     * Scheme: https
     * host: www.jianshu.com
     * path: /p/1cbaf784c29c
     * url = scheme + "://" + host + path;
     */
    private void getDataFromBrowser(Intent intent) {
        Uri data = intent.getData();
        if (data != null) {
            try {
                String scheme = data.getScheme();
                String host = data.getHost();
                String path = data.getPath();
                String text = "Scheme: " + scheme + "\n" + "host: " + host + "\n" + "path: " + path;
                Log.e("data", text);
                String url = scheme + "://" + host + path;
                byWebView.loadUrl(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 直接通过三方浏览器打开时，回退到首页
     */
    public void handleFinish() {
        supportFinishAfterTransition();
        /*if (!MainActivity.isLaunch) {
            MainActivity.start(this);
        }*/
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (byWebView.handleKeyEvent(keyCode, event)) {
            return true;
        } else {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                handleFinish();
            }
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        byWebView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        byWebView.onResume();
    }

    @Override
    protected void onDestroy() {
        byWebView.onDestroy();
        super.onDestroy();
    }

    /**
     * 打开网页:
     *
     * @param mContext 上下文
     * @param url      要加载的网页url
     * @param title    标题
     * @param state    类型
     */
    public static void loadUrl(Context mContext, String url, String title, int state) {
        Intent intent = new Intent(mContext, ByWebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("state", state);
        intent.putExtra("title", title == null ? "加载中..." : title);
        mContext.startActivity(intent);
    }

    class MyJavascriptInterface {
        @JavascriptInterface
        public void postMessage(String jsonString) {
            LogUtils.e("h5Json>>>>>", jsonString);
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONObject data = new JSONObject();
                data.put("action", jsonObject.getString("action"));
                data.put("id", jsonObject.getString("id"));
                data.put("msg", "");
                if (jsonObject.getString("action").equals("timeSDK")) {
                    timeManager.setJsonString(jsonString);
                    EasyPermissions.requestPermissions(ByWebViewActivity.this, "需要获取电话存储权限", timeManager.TIME_REQUEST_PERMISSION, timeManager.allPermissions);
                } else if (jsonObject.getString("action").equals("getLoginInfo")) {
                    JSONObject value = new JSONObject();
                    data.put("result", "ok");
                    value.put("token", BaseCacheManager.getUserTemp().getToken());
                    data.put("data", value);
                    byWebView.getWebView().post(new Runnable() {
                        @Override
                        public void run() {
                            byWebView.loadUrl("javascript:" + "webViewToLogin" + "(" + data.toString() + ")");
                        }
                    });

                } else if (jsonObject.getString("action").equals("getPackageName")) {
                    JSONObject value = new JSONObject();
                    data.put("result", "ok");
                    value.put("packageName", BuildConfig.APPLICATION_ID);
                    value.put("androidId", DeviceUtils.getAndroidId(BaseApplication.getAppContext()));
                    value.put("imei", DeviceUtils.getIMEI(BaseApplication.getAppContext()));
                    value.put("gaid", BaseCacheManager.getUserTemp().getGaid());
                    value.put("afid", "afid");
                    value.put("appVersion", BuildConfig.VERSION_NAME);
                    value.put("appName", DeviceUtils.getAppName(BaseApplication.getAppContext()));
                    data.put("data", value);

                    byWebView.getWebView().post(new Runnable() {
                        @Override
                        public void run() {
                            byWebView.loadUrl("javascript:" + "webViewGetPackageName" + "(" + data.toString() + ")");
                        }
                    });
                } else if (jsonObject.getString("action").equals("getVersionName")) {
                    JSONObject value = new JSONObject();
                    data.put("result", "ok");
                    value.put("versionName", BuildConfig.VERSION_NAME);
                    data.put("data", value);

                    byWebView.getWebView().post(new Runnable() {
                        @Override
                        public void run() {
                            byWebView.loadUrl("javascript:" + "webViewVersionName" + "(" + data.toString() + ")");
                        }
                    });
                } else if (jsonObject.getString("action").equals("toLogin")) {
                    JSONObject value = new JSONObject();
                    data.put("result", "ok");
                    value.put("token", BaseCacheManager.getUserTemp().getToken());
                    data.put("data", value);

                    byWebView.getWebView().post(new Runnable() {
                        @Override
                        public void run() {
                            byWebView.loadUrl("javascript:" + "webViewToLogin" + "(" + data.toString() + ")");
                        }
                    });
                } else if (jsonObject.getString("action").equals("logout")) {
                    byWebView.getWebView().post(new Runnable() {
                        @Override
                        public void run() {
                            if (!logout) {
                                BaseCacheManager.getUserTemp().clear();
                                Intent intent = new Intent(ByWebViewActivity.this, LoginActivity.class);
                                ByWebViewActivity.this.startActivity(intent);
                                ByWebViewActivity.this.finish();
                                byWebView.loadUrl("javascript:webViewLoginOut()");
                                logout = true;
                            }
                        }
                    });
                } else if (jsonObject.getString("action").equals("selectContact")) {
                    EasyPermissions.requestPermissions(ByWebViewActivity.this, "需要获取电话权限", PHONE_REQUEST_PERMISSION, permsPhone);
                    if (EasyPermissions.hasPermissions(ByWebViewActivity.this, permsPhone)) {
                        selectConnection();
                        if (!TextUtils.isEmpty(phoneNumber)) {
                            JSONObject jsonObjectData = jsonObject.optJSONObject("data");
                            JSONObject value = new JSONObject();
                            value.put("isSelectContact", jsonObjectData.getBoolean("isSelectContact"));
                            value.put("selectContactIndex", jsonObjectData.getInt("selectContactIndex"));
                            value.put("name", phoneName); //姓名
                            value.put("phone", phoneNumber);
                            data.put("result", "ok");
                            data.put("data", value);

                            byWebView.getWebView().post(new Runnable() {
                                @Override
                                public void run() {
                                    byWebView.loadUrl("javascript:" + "getWebViewSelectContact" + "(" + data.toString() + ")");
                                }
                            });
                        }


                    }
                } else if (jsonObject.getString("action").equals("getAccuauthSDK")) {

                    GuardianLivenessDetectionSDK.letSDKHandleCameraPermission();
                    if (GuardianLivenessDetectionSDK.isSDKHandleCameraPermission()) {
                        Intent intent = new Intent(ByWebViewActivity.this, LivenessActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_LIVENESS);
                    }

                    byWebView.getWebView().post(new Runnable() {
                        @Override
                        public void run() {
                            byWebView.loadUrl("javascript:" + "webViewFaceImg" + "(" + data.toString() + ")");

                        }
                    });

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void selectConnection() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    private void getContacts(Intent data) {
        if (data == null) {
            return;
        }

        Uri contactData = data.getData();
        if (contactData == null) {
            return;
        }


        Uri contactUri = data.getData();
        Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
        if (cursor.moveToFirst()) {
            phoneName = cursor
                    .getString(cursor
                            .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String hasPhone = cursor
                    .getString(cursor
                            .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            String id = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.Contacts._ID));
            if (hasPhone.equalsIgnoreCase("1")) {
                hasPhone = "true";
            } else {
                hasPhone = "false";
            }
            if (Boolean.parseBoolean(hasPhone)) {
                Cursor phones = getContentResolver()
                        .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                        + " = " + id, null, null);
                while (phones.moveToNext()) {
                    phoneNumber = phones
                            .getString(phones
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                phones.close();
            }
            cursor.close();
        }
    }


}

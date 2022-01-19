package com.mmt.smartloan.view.webview;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
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
import com.mmt.smartloan.bean.UpdateInfoBean;
import com.mmt.smartloan.cache.BaseCacheManager;
import com.mmt.smartloan.databinding.ActivityByWebviewBinding;
import com.mmt.smartloan.module.WebViewModule;
import com.mmt.smartloan.repository.AppViewModelFactory;
import com.mmt.smartloan.utils.BitmapUtils;
import com.mmt.smartloan.utils.LogUtils;
import com.mmt.smartloan.utils.device.DeviceUtils;
import com.mmt.smartloan.view.FloatWindow;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import ai.advance.liveness.lib.LivenessResult;
import ai.advance.liveness.sdk.activity.LivenessActivity;
import constant.UiType;
import listener.OnBtnClickListener;
import model.UiConfig;
import model.UpdateConfig;
import update.UpdateAppUtils;


public class ByWebViewActivity extends BaseActivity<ActivityByWebviewBinding, WebViewModule> {
    public final int PHONE_REQUEST_PERMISSION = 8848;
    public final int CAMERA_REQUEST_PERMISSION = 8846;
    public static final int REQUEST_CODE_LIVENESS = 8847;
    public final int PICK_CONTACT = 7747;
    private JSONObject jsonObject;
    private JSONObject data;
    private FloatWindow floatWindow;
    private String[] permsPhone = {Manifest.permission.READ_CONTACTS};
    private String[] permsCamera = {Manifest.permission.CAMERA};
    public String[] allPermissions = new String[]{"android.permission.READ_PHONE_STATE", "android.permission.READ_CONTACTS", "android.permission.READ_SMS", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION", "Manifest.permission.CAMERA"};


    // 网页链接
    private int mState;
    private String mUrl;
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
        /*// 权限判断
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                // 启动Activity让用户授权
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 10);
            } else {
                // 执行6.0以上绘制代码
                initFloatWindow();
            }
        } else {
            // 执行6.0以下绘制代码
            initFloatWindow();
        }*/

        UpdateAppUtils.init(BaseApplication.getAppContext());
        viewModel.getNewVersion();
        getIntentData();
        initTitle();
        getDataFromBrowser(getIntent());

    }

    private void getIntentData() {
        mUrl = getIntent().getStringExtra("url");
        mState = getIntent().getIntExtra("state", 0);
    }

    private void initFloatWindow() {
        floatWindow = new FloatWindow(getApplicationContext());
        //floatWindow.showFloatWindow();
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
            if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)) {
                selectConnection();
            }
        }
        if (requestCode == CAMERA_REQUEST_PERMISSION) {
            if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
                Intent intent = new Intent(ByWebViewActivity.this, LivenessActivity.class);
                startActivityForResult(intent, REQUEST_CODE_LIVENESS);
            }

        }
        if (requestCode == 0x123) {
            if (byWebView.getmWebChromeClient().permissionGranted()) {
                startActivityForResult(byWebView.getmWebChromeClient().createCameraIntent(),
                        byWebView.getmWebChromeClient().FILECHOOSER_RESULTCODE);
            } else {
                if (byWebView.getmWebChromeClient().valueCallbacks != null) {
                    byWebView.getmWebChromeClient().valueCallbacks.onReceiveValue(null);
                }
            }

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
            if (intent != null) {
                Uri uri = intent.getData();
                String[] contact = getPhoneContacts(uri);
                if (contact != null) {
                    JSONObject jsonObjectData = jsonObject.optJSONObject("data");
                    JSONObject value = new JSONObject();
                    try {
                        value.put("isSelectContact", jsonObjectData.getBoolean("isSelectContact"));
                        value.put("selectContactIndex", jsonObjectData.getInt("selectContactIndex"));
                        value.put("name", contact[0]); //姓名
                        value.put("phone", contact[1]);
                        data.put("result", "ok");
                        data.put("data", value);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    byWebView.getWebView().post(new Runnable() {
                        @Override
                        public void run() {
                            byWebView.loadUrl("javascript:" + "getWebViewSelectContact" + "(" + data.toString() + ")");
                        }
                    });
                }

            }
        } else if (requestCode == REQUEST_CODE_LIVENESS) {
            if (LivenessResult.isSuccess()) {// 活体检测成功
                String livenessId = LivenessResult.getLivenessId();// 本次活体id
                Bitmap livenessBitmap = LivenessResult.getLivenessBitmap();// 本次活体图片
                JSONObject value = new JSONObject();
                try {
                    value.put("livenessId", livenessId);
                    value.put("file", BitmapUtils.bitmapToBase64(livenessBitmap));
                    data.put("result", "ok");
                    data.put("data", value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {// 活体检测失败
                String errorCode = LivenessResult.getErrorCode();// 失败错误码
                String errorMsg = LivenessResult.getErrorMsg();// 失败原因
                JSONObject value = new JSONObject();
                try {
                    value.put("msg", errorMsg);
                    data.put("result", "fail");
                    data.put("data", value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            byWebView.getWebView().post(new Runnable() {
                @Override
                public void run() {
                    byWebView.loadUrl("javascript:" + "webViewFaceImg" + "(" + data.toString() + ")");

                }
            });
        } else if (requestCode == byWebView.getmWebChromeClient().FILECHOOSER_RESULTCODE) {
            Uri[] uris = new Uri[1];
            uris[0] = byWebView.getmWebChromeClient().myImageUri;
            byWebView.getmWebChromeClient().update(uris);
            // byWebView.handleFileChooser(requestCode, resultCode, intent);
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
        intent.putExtra("title", title == null ? "Cargando..." : title);
        mContext.startActivity(intent);
    }

    class MyJavascriptInterface {
        @SuppressLint("RestrictedApi")
        @JavascriptInterface
        public void postMessage(String jsonString) {
            LogUtils.e("h5Json>>>>>", jsonString);
            try {
                jsonObject = new JSONObject(jsonString);
                data = new JSONObject();
                data.put("action", jsonObject.getString("action"));
                data.put("id", jsonObject.getString("id"));
                data.put("msg", "");
                if (jsonObject.getString("action").equals("timeSDK")) {
                    timeManager.setJsonString(jsonString);
                    if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(ByWebViewActivity.this, android.Manifest.permission.READ_PHONE_STATE)
                            && PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(ByWebViewActivity.this, android.Manifest.permission.READ_CONTACTS)
                            && PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(ByWebViewActivity.this, android.Manifest.permission.READ_SMS) &&
                            PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(ByWebViewActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) &&
                            PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(ByWebViewActivity.this, android.Manifest.permission.CAMERA) &&
                            PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(ByWebViewActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                            PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(ByWebViewActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) &&
                            PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(ByWebViewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    ) {
                        timeManager.onRequestPermission();
                    } else {
                        ActivityCompat.requestPermissions(ByWebViewActivity.this, allPermissions, timeManager.TIME_REQUEST_PERMISSION);
                    }

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
                    if (!logout) {
                        BaseCacheManager.getUserTemp().clear();
                        Intent intent = new Intent(ByWebViewActivity.this, LoginActivity.class);
                        ByWebViewActivity.this.startActivity(intent);
                        ByWebViewActivity.this.finish();
                        logout = true;
                    }
                    //日志收集
                } else if (jsonObject.getString("action").equals("logEventByLocal")) {
                    JSONObject isUploadData = jsonObject.optJSONObject("data");
                    boolean isUpload = isUploadData.getBoolean("isUpload");
                    {
                        if (isUpload) {
                            viewModel.logEventByLocal();

                        } else {

                        }
                    }
                    //AF日志收集
                } else if (jsonObject.getString("action").equals("logEventByAF")) {


                } else if (jsonObject.getString("action").equals("selectContact")) {

                    if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(ByWebViewActivity.this, android.Manifest.permission.READ_CONTACTS)
                    ) {
                        selectConnection();
                    } else {
                        ActivityCompat.requestPermissions(ByWebViewActivity.this, permsPhone, PHONE_REQUEST_PERMISSION);
                    }
                } else if (jsonObject.getString("action").equals("getAccuauthSDK")) {
                    if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(ByWebViewActivity.this, Manifest.permission.CAMERA)
                    ) {
                        Intent intent = new Intent(ByWebViewActivity.this, LivenessActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_LIVENESS);
                    } else {
                        ActivityCompat.requestPermissions(ByWebViewActivity.this, permsCamera, CAMERA_REQUEST_PERMISSION);
                    }


                } else if (jsonObject.getString("action").equals("setNewToken")) {
                    JSONObject value = new JSONObject();
                    value.put("token", BaseCacheManager.getUserTemp().getToken());
                    value.put("phoneNumber", BaseCacheManager.getUserTemp().getPhone());
                    data.put("result", "ok");
                    data.put("data", value);

                    byWebView.getWebView().post(new Runnable() {
                        @Override
                        public void run() {
                            byWebView.loadUrl("javascript:" + "setNewToken" + "(" + data.toString() + ")");
                        }
                    });
                } else if (jsonObject.getString("action").equals("ToWhatsapp")) {
                    JSONObject value = new JSONObject();
                    value.put("phone", "this.whatsapp");
                    data.put("result", "ok");
                    data.put("data", value);

                    byWebView.getWebView().post(new Runnable() {
                        @Override
                        public void run() {
                            byWebView.loadUrl("javascript:" + "webViewToWhatsapp" + "(" + data.toString() + ")");
                        }
                    });
                } else if (jsonObject.getString("action").equals("toGooglePlayer")) {
                    JSONObject value = new JSONObject();
                    value.put("packageId", "item.downloadLink");
                    data.put("result", "ok");
                    data.put("data", value);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void selectConnection() {

        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    /**
     * 读取联系人信息
     *
     * @param uri
     */
    private String[] getPhoneContacts(Uri uri) {
        String[] contact = new String[2];
        //得到ContentResolver对象
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            //取得联系人姓名
            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            contact[0] = cursor.getString(nameFieldColumnIndex);
            contact[1] = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Log.i("contacts", contact[0]);
            Log.i("contactsUsername", contact[1]);
            cursor.close();
        } else {
            return null;
        }
        return contact;
    }
    /*
     */

    /**
     * 退出app
     *//*
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void exitApp() {
        ActivityManager manager = (ActivityManager) globalContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            manager.getAppTasks().forEach(appTask -> appTask.finishAndRemoveTask());
        } else {
            System.exit(0);
        }
    }*/
    public void updateApp(UpdateInfoBean data) {
        if (data != null) {
            if (Integer.parseInt(data.getVersionCode()) > BuildConfig.VERSION_CODE) {
                UpdateConfig updateConfig = new UpdateConfig();
                updateConfig.setCheckWifi(false);
                updateConfig.setDebug(false);
                updateConfig.setForce(data.isForcedUpdate());
                updateConfig.setShowNotification(false);
                updateConfig.setAlwaysShow(true);
                UiConfig uiConfig = new UiConfig();
                uiConfig.setUiType(UiType.CUSTOM);
                uiConfig.setCustomLayoutId(R.layout.view_update_dialog);
                UpdateAppUtils
                        .getInstance()
                        .apkUrl(data.getLink())
                        .updateTitle("Nueva versión disponible")
                        .updateContent("Por favor, actualice a la última versión")
                        .uiConfig(uiConfig)
                        .updateConfig(updateConfig)
                        .setUpdateBtnClickListener(new OnBtnClickListener() {
                            @Override
                            public boolean onClick() {
                                transferToGooglePlay(data.getLink());
                                return true;
                            }
                        })
                        .update();
            }

        }
    }

    /**
     * 跳转到谷歌市场
     */
    public void transferToGooglePlay(String packageId) {

        if (!TextUtils.isEmpty(packageId)) {
            if (packageId.startsWith("http")) {
                Intent intentGp = new Intent(Intent.ACTION_VIEW);
                intentGp.setData(Uri.parse(packageId));
                startActivity(intentGp);
                return;
            }
        }

        if (TextUtils.isEmpty(packageId)) {
            packageId = BuildConfig.APPLICATION_ID;
        }
        try {
            Uri uri = Uri.parse("market://details?id=" + packageId);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.android.vending");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
            //存在手机里没安装应用市场的情况，跳转会包异常，做一个接收判断
            if (intent.resolveActivity(getPackageManager()) != null) { //可以接收
                startActivity(intent);
            } else { //没有应用市场，我们通过浏览器跳转到Google Play
                Intent intent2 = new Intent(Intent.ACTION_VIEW);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent2.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + packageId));
                //这里存在一个极端情况就是有些用户浏览器也没有，再判断一次
                if (intent2.resolveActivity(getPackageManager()) != null) { //有浏览器
                    startActivity(intent2);
                } else { //
                    Toast.makeText(ByWebViewActivity.this, "No tienes instalado un mercado de aplicaciones, ¡ni siquiera un navegador!", Toast.LENGTH_SHORT).show();

                }
            }
        } catch (Exception e) {
            Toast.makeText(ByWebViewActivity.this, e.toString(), Toast.LENGTH_SHORT).show();

        }
    }


}

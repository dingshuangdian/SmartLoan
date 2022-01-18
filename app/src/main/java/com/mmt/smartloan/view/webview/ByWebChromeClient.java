package com.mmt.smartloan.view.webview;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.mmt.smartloan.R;
import com.mmt.smartloan.base.BaseApplication;
import com.mmt.smartloan.utils.BitmapUtil_;
import com.mmt.smartloan.utils.BitmapUtils;
import com.mmt.smartloan.utils.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ai.advance.common.utils.BitmapUtil;
import ai.advance.liveness.sdk.activity.LivenessActivity;


/**
 * Created by jingbin on 2019/07/27.
 * - 播放网络视频配置
 * - 上传图片(兼容)
 */
public class ByWebChromeClient extends WebChromeClient {
    private Uri imageUri;
    private WeakReference<Activity> mActivityWeakReference = null;
    private ByWebView mByWebView;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadMessageForAndroid5;
    private ValueCallback<Uri[]> mUploadMessageForAndroid56;
    private static int RESULT_CODE_FILE_CHOOSER = 1;
    private static int RESULT_CODE_FILE_CHOOSER_FOR_ANDROID_5 = 2;
    private View mProgressVideo;
    private View mCustomView;
    private CustomViewCallback mCustomViewCallback;
    private ByFullscreenHolder videoFullView;
    private OnTitleProgressCallback onByWebChromeCallback;
    // 修复可能部分h5无故横屏问题
    private boolean isFixScreenLandscape = false;
    // 修复可能部分h5无故竖屏问题
    private boolean isFixScreenPortrait = false;

    ByWebChromeClient(Activity activity, ByWebView byWebView) {
        mActivityWeakReference = new WeakReference<Activity>(activity);
        this.mByWebView = byWebView;
    }

    void setOnByWebChromeCallback(OnTitleProgressCallback onByWebChromeCallback) {
        this.onByWebChromeCallback = onByWebChromeCallback;
    }

    public void setFixScreenLandscape(boolean fixScreenLandscape) {
        isFixScreenLandscape = fixScreenLandscape;
    }

    public void setFixScreenPortrait(boolean fixScreenPortrait) {
        isFixScreenPortrait = fixScreenPortrait;
    }

    /**
     * 播放网络视频时全屏会被调用的方法
     */
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        Activity mActivity = this.mActivityWeakReference.get();
        if (mActivity != null && !mActivity.isFinishing()) {
            if (!isFixScreenLandscape) {
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            mByWebView.getWebView().setVisibility(View.INVISIBLE);

            // 如果一个视图已经存在，那么立刻终止并新建一个
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }

            FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
            videoFullView = new ByFullscreenHolder(mActivity);
            videoFullView.addView(view);
            decor.addView(videoFullView);

            mCustomView = view;
            mCustomViewCallback = callback;
            videoFullView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 视频播放退出全屏会被调用的
     */
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onHideCustomView() {
        Activity mActivity = this.mActivityWeakReference.get();
        if (mActivity != null && !mActivity.isFinishing()) {
            // 不是全屏播放状态
            if (mCustomView == null) {
                return;
            }
            // 还原到之前的屏幕状态
            if (!isFixScreenPortrait) {
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            mCustomView.setVisibility(View.GONE);
            if (videoFullView != null) {
                videoFullView.removeView(mCustomView);
                videoFullView.setVisibility(View.GONE);
            }
            mCustomView = null;
            mCustomViewCallback.onCustomViewHidden();
            mByWebView.getWebView().setVisibility(View.VISIBLE);
        }
    }

    /**
     * 视频加载时loading
     */
    @Override
    public View getVideoLoadingProgressView() {
        if (mProgressVideo == null) {
            mProgressVideo = LayoutInflater.from(mByWebView.getWebView().getContext()).inflate(R.layout.by_video_loading_progress, null);
        }
        return mProgressVideo;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        // 进度条
        if (mByWebView.getProgressBar() != null) {
            mByWebView.getProgressBar().setWebProgress(newProgress);
        }
        // 当显示错误页面时，进度达到100才显示网页
        if (mByWebView.getWebView() != null
                && mByWebView.getWebView().getVisibility() == View.INVISIBLE
                && (mByWebView.getErrorView() == null || mByWebView.getErrorView().getVisibility() == View.GONE)
                && newProgress == 100) {
            mByWebView.getWebView().setVisibility(View.VISIBLE);
        }
        if (onByWebChromeCallback != null) {
            onByWebChromeCallback.onProgressChanged(newProgress);
        }
    }

    /**
     * 判断是否是全屏
     */
    boolean inCustomView() {
        return (mCustomView != null);
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        // 设置title
        if (onByWebChromeCallback != null) {
            if (mByWebView.getErrorView() != null && mByWebView.getErrorView().getVisibility() == View.VISIBLE) {
                onByWebChromeCallback.onReceivedTitle(TextUtils.isEmpty(mByWebView.getErrorTitle()) ? "网页无法打开" : mByWebView.getErrorTitle());
            } else {
                onByWebChromeCallback.onReceivedTitle(title);
            }
        }
    }

    //扩展浏览器上传文件
    //3.0++版本
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        openFileChooserImpl(uploadMsg);
    }

    //3.0--版本
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        openFileChooserImpl(uploadMsg);
    }

    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        openFileChooserImpl(uploadMsg);
    }

    // For Android > 5.0
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> uploadMsg, FileChooserParams fileChooserParams) {
        mUploadMessageForAndroid5 = uploadMsg;
        mUploadMessageForAndroid56 = uploadMsg;
        takePhoto();
        return true;
    }

    @Override
    public void onPermissionRequestCanceled(PermissionRequest request) {
        super.onPermissionRequestCanceled(request);


    }

    public boolean permissionGranted() {
        Activity mActivity = this.mActivityWeakReference.get();
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.CAMERA)
                && PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                && PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        Activity mActivity = this.mActivityWeakReference.get();
        if (mActivity != null && !mActivity.isFinishing()) {
            mUploadMessage = uploadMsg;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            mActivity.startActivityForResult(Intent.createChooser(intent, "文件选择"), RESULT_CODE_FILE_CHOOSER);
        }
    }

    private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {
        Activity mActivity = this.mActivityWeakReference.get();
        if (mActivity != null && !mActivity.isFinishing()) {
            mUploadMessageForAndroid5 = uploadMsg;
            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("image/*");

            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "图片选择");

            mActivity.startActivityForResult(chooserIntent, RESULT_CODE_FILE_CHOOSER_FOR_ANDROID_5);
        }
    }

    /**
     * 5.0以下 上传图片成功后的回调
     */
    private void uploadMessage(Intent intent, int resultCode) {
        if (null == mUploadMessage) {
            return;
        }
        Uri result = intent == null || resultCode != Activity.RESULT_OK ? null : intent.getData();
        mUploadMessage.onReceiveValue(result);
        mUploadMessage = null;
    }

    /**
     * 5.0以上 上传图片成功后的回调
     */
    private void uploadMessageForAndroid5(Intent intent, int resultCode) {
        if (null == mUploadMessageForAndroid5) {
            return;
        }
        Activity mActivity = this.mActivityWeakReference.get();


        Uri result = (intent == null || resultCode != Activity.RESULT_OK) ? null : intent.getData();
        if (result != null) {
            mUploadMessageForAndroid5.onReceiveValue(new Uri[]{Uri.fromFile(BitmapUtils.compressImage(FileUtil.getFileAbsolutePath(BaseApplication.getAppContext(), result), 1080, 1920))});
        } else if (imageUri != null && checkURIResource(BaseApplication.getAppContext(), imageUri)) {

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(mActivity.getContentResolver().
                        openInputStream(imageUri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(mActivity.getContentResolver(), BitmapUtil_.comp(bitmap), null, null));
            mUploadMessageForAndroid5.onReceiveValue(new Uri[]{uri});
        }
        mUploadMessageForAndroid5 = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onPermissionRequest(PermissionRequest request) {
        super.onPermissionRequest(request);
        request.grant(request.getResources());
    }


    private boolean checkURIResource(Context context, Uri uri) {
        boolean bool = false;
        if (null != uri) {
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                inputStream.close();
                bool = true;
            } catch (Exception e) {
                Log.w("MY_TAG", "File corresponding to the uri does not exist" + uri.toString());
                mUploadMessageForAndroid5.onReceiveValue(null);
            }
        }
        return bool;
    }


    public void takePhoto() {
        Activity mActivity = this.mActivityWeakReference.get();
        if (!permissionGranted()) {
            String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(mActivity, perms, 0x123);
            mUploadMessageForAndroid5.onReceiveValue(null);
            return;
        }
        String filePath = Environment.getExternalStorageDirectory() + File.separator
                + Environment.DIRECTORY_PICTURES + File.separator;
        String fileName = "IMG_" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
        imageUri = getUriForFile(mActivity, new File(filePath + fileName));
        mUploadMessageForAndroid5 = mUploadMessageForAndroid56;


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        mActivity.startActivityForResult(intent, RESULT_CODE_FILE_CHOOSER_FOR_ANDROID_5);
       /* Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        *//*Intent Photo = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        Intent chooserIntent = Intent.createChooser(Photo, "");*//*
        captureIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});

        mActivity.startActivityForResult(captureIntent, RESULT_CODE_FILE_CHOOSER_FOR_ANDROID_5);*/
    }

    private static Uri getUriForFile(Context context, File file) {
        if (context == null || file == null) {
            throw new NullPointerException();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(context.getApplicationContext(), "com.mmt.smartloan.fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    /**
     * 用于Activity的回调
     */
    public void handleFileChooser(int requestCode, int resultCode, Intent intent) {
        if (requestCode == RESULT_CODE_FILE_CHOOSER) {
            uploadMessage(intent, resultCode);
        } else if (requestCode == RESULT_CODE_FILE_CHOOSER_FOR_ANDROID_5) {
            uploadMessageForAndroid5(intent, resultCode);
        }
    }

    ByFullscreenHolder getVideoFullView() {
        return videoFullView;
    }

    @Nullable
    @Override
    public Bitmap getDefaultVideoPoster() {
        if (super.getDefaultVideoPoster() == null) {
            return BitmapFactory.decodeResource(mByWebView.getWebView().getResources(), R.mipmap.by_icon_video);
        } else {
            return super.getDefaultVideoPoster();
        }
    }


}

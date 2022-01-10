package com.mmt.smartloan.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.google.android.gms.analytics.CampaignTrackingReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <p>版权©️所有：大参林医药集团<p>
 * <p>作者：dingshuangdian<p>
 * <p>创建时间：2022/1/10<p>
 */
public class InstallReferrerBroadcastReceiver extends BroadcastReceiver {

    private InstallReferrerClient mReferrerClient;
    private Context context;
    private Intent intent;

    @Override
    public void onReceive(Context context, Intent intent) {
        int appVersionCode = getAppVersionCode(context);
        this.context = context;
        this.intent = intent;

        String mAction = intent.getAction();
        LogUtils.e("收到广播的回调mAction=" + mAction);

        if (appVersionCode < 80837300) {
            LogUtils.e("旧版获取渠道来源数据");
            getInstallReferrerData();
        } else {
            LogUtils.e("新版获取渠道来源数据");
            getConnect();
        }
    }

    /**
     * Google Play版本<8.3.73时获取安装来源数据
     */
    private void getInstallReferrerData() {
        Bundle extras = intent.getExtras();
        String referrer = "";
        if (extras != null) {
            referrer = extras.getString("referrer");
            // 格式：utm_source=testSource&utm_medium=testMedium&utm_term=testTerm&utm_content=11
            upLoadinstallReferrer(referrer);
        }
        new CampaignTrackingReceiver().onReceive(context, intent);//调用谷歌广播的方法
    }


    /**
     * 获取版本号
     *
     * @return Google Play应用的版本号
     */
    public static int getAppVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo("com.android.vending", 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 与谷歌商店建立连接
     */
    private void getConnect() {
        mReferrerClient = InstallReferrerClient.newBuilder(context).build();
        mReferrerClient.startConnection(installReferrerStateListener);
    }

    private InstallReferrerStateListener installReferrerStateListener = new InstallReferrerStateListener() {
        @Override
        public void onInstallReferrerSetupFinished(int responseCode) {
            switch (responseCode) {
                case InstallReferrerClient.InstallReferrerResponse.OK:
                    // Connection established
//                    Toast.makeText(context, "与谷歌商店连接成功", Toast.LENGTH_LONG).show();
                    LogUtils.e("与谷歌商店连接成功");
                    getMessage();
                    break;
                case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                    // API not available on the current Play Store app
//                    Toast.makeText(context, "与谷歌商店连接失败", Toast.LENGTH_LONG).show();
                    LogUtils.e("与谷歌商店连接失败败：API not available on the current Play Store app");
                    break;
                case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                    // Connection could not be established
                    LogUtils.e("InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE");
//                    Toast.makeText(context, "与谷歌商店连接失败", Toast.LENGTH_LONG).show();
                    break;
            }
        }

        @Override
        public void onInstallReferrerServiceDisconnected() {
            // Try to restart the connection on the next request to
            // Google Play by calling the startConnection() method.
            LogUtils.e("onInstallReferrerServiceDisconnected()");
            //重新连接
            getConnect();
        }
    };

    /**
     * Google Play版本>=8.3.73时获取安装来源数据
     */

    private void getMessage() {
        try {
            ReferrerDetails response = mReferrerClient.getInstallReferrer();
            String installReferrer = response.getInstallReferrer();
            long referrerClickTimestampSeconds = response.getReferrerClickTimestampSeconds();
            installReferrer = installReferrer + "&" + "referrerClickTimestampSeconds=" + referrerClickTimestampSeconds;
            long installBeginTimestampSeconds = response.getInstallBeginTimestampSeconds();
            installReferrer = installReferrer + "&" + "installBeginTimestampSeconds=" + installBeginTimestampSeconds;

            upLoadinstallReferrer(installReferrer);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        new CampaignTrackingReceiver().onReceive(context, intent);//调用谷歌广播的方法
    }


    /**
     * 上传数据到服务器
     */
    private void upLoadinstallReferrer(String referer) {
        JSONObject object = getSplitData(referer);
        //下面做自己上传数据到服务端的操作，数据为object，要加参数自己加
    }


    /**
     * 把格式：utm_source=testSource&utm_medium=testMedium&utm_term=testTerm&utm_content=11
     * 这种格式的数据切割成key,value的形式并put进JSONObject对象，用于上传
     *
     * @param referer
     * @return
     */
    private JSONObject getSplitData(String referer) {
        JSONObject object = new JSONObject();
        for (String data : referer.split("&")) {
            String[] split = data.split("=");
            for (int i = 0; i < split.length; i++) {
                try {
                    object.put(split[0], split[1]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return object;
    }
}

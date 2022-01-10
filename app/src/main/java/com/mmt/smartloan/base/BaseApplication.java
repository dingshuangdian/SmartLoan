package com.mmt.smartloan.base;

import android.app.Application;
import android.content.Context;

import com.mmt.smartloan.utils.DebugUtils;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

/**
 * <p>版权©️所有：大参林医药集团<p>
 * <p>作者：dingshuangdian<p>
 * <p>创建时间：2022/1/10<p>
 */
public class BaseApplication extends Application {
    public static Context appContext;
    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        Logger.addLogAdapter(new AndroidLogAdapter());
        DebugUtils.initDebugState();
    }

    public static Context getAppContext() {
        return appContext;
    }
}

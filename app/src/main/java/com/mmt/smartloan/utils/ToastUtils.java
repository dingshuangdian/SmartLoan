package com.mmt.smartloan.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationManagerCompat;

import com.mmt.smartloan.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static android.content.Context.WINDOW_SERVICE;

public class ToastUtils {
    private static Toast toast = null;

    private static Object iNotificationManagerObj;

    private static Toast setToast(Context context, String content) {

        Configuration configuration = context.getApplicationContext().getResources().getConfiguration();
        configuration.fontScale = 1;
        //0.85 小, 1 标准大小, 1.15 大，1.3 超大 ，1.45 特大
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        context.getApplicationContext().getResources().updateConfiguration(configuration, metrics);

        if (toast == null) {
            toast = new Toast(context);
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        ConstraintLayout toastLayout = (ConstraintLayout)
                LayoutInflater.from(context).inflate(R.layout.item_toast_utils, null);
        TextView txtToast = toastLayout.findViewById(R.id.tv_toast);
        txtToast.setText(content);
        toast.setView(toastLayout);
        return toast;
    }

    /**
     * 显示对话框 短 *
     *
     * @param content 要显示的内容
     */
    public static void showToast(String content) {
        showCommonToast(ContextHolder.getContext(), content, false);
    }

    /**
     * 显示对话框 ⻓长 *
     *
     * @param content 要显示的内容
     */
    public static void showToastLong(String content) {
        showCommonToast(ContextHolder.getContext(), content, true);
    }

    /**
     * @param context
     * @param message
     */
    private static void show(Context context, String message, boolean isLong, int duration) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        //后setText 兼容⼩小⽶米默认会显示app名称的问题
        final Toast btoast = setToast(context, message);
        if (duration <= 0) {
            btoast.setDuration(isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        } else {
            btoast.setDuration(duration);
        }
        //toast.setText(message);
        if (isNotificationEnabled(context)) {
            btoast.show();

            if (duration > 0) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btoast.cancel();
                    }
                }, duration);
            }
        } else {
            showSystemToast(btoast, duration);
        }

    }

    /**
     * 业务相关的toast
     *
     * @param context
     * @param message
     */
    private static void showCommonToast(final Context context, final String message, final boolean islong) {
        if (context != null) {
            if (context instanceof Activity) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        show(context, message, islong, 0);
                    }
                });
            } else {
                show(context, message, islong, 0);
            }
        }
    }

    /**
     * 显示系统Toast
     */
    private static void showSystemToast(final Toast toast, final int duration) {
        try {
            @SuppressLint("SoonBlockedPrivateApi") Method getServiceMethod = Toast.class.getDeclaredMethod("getService");
            getServiceMethod.setAccessible(true);

            //hook INotificationManager
            if (iNotificationManagerObj == null) {
                iNotificationManagerObj = getServiceMethod.invoke(null);
                Class iNotificationManagerCls = Class.forName("android.app.INotificationManager");
                Object iNotificationManagerProxy = Proxy.newProxyInstance(toast.getClass().getClassLoader(), new Class[]{iNotificationManagerCls}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        //强制使⽤用系统Toast
                        if ("enqueueToast".equals(method.getName())
                                || "enqueueToastEx".equals(method.getName())) { //华为 p20 pro上为enqueueToastEx
                            args[0] = "android";
                        }
                        return method.invoke(iNotificationManagerObj, args);
                    }
                });
                Field sServiceFiled = Toast.class.getDeclaredField("sService");
                sServiceFiled.setAccessible(true);
                sServiceFiled.set(null, iNotificationManagerProxy);
            }
            toast.show();
            if (duration > 0) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, duration);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 消息通知是否开启 *
     *
     * @return
     */
    private static boolean isNotificationEnabled(Context context) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        boolean areNotificationsEnabled = notificationManagerCompat.areNotificationsEnabled();
        return areNotificationsEnabled;
    }
}

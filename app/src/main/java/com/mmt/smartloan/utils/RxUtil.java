package com.mmt.smartloan.utils;

import android.app.Activity;

import com.mmt.smartloan.base.BaseResult;
import com.mmt.smartloan.rxjava.exception.core.GlobalErrorTransformer;
import com.mmt.smartloan.rxjava.exception.core.GlobalTransformer;
import com.mmt.smartloan.rxjava.exception.retry.RetryConfig;
import com.mmt.smartloan.rxjava.network.NetworkCode;

import org.json.JSONException;

import java.net.ConnectException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import retrofit2.HttpException;

/**
 * RxJava操作工具类
 *
 * @desc: RxJava操作工具类
 * @data: 2018-11-19.
 * @time: 19:55
 */
public class RxUtil {
    private static final String TAG = RxUtil.class.getCanonicalName();

    /**
     * Wrapper参数Builder, 等于调用@see getWrapper()
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    public static <T> GlobalTransformer<T> getWrapper() {
        return getWrapper(true, new RetryConfig(), true, false, "加载中···", null);
    }

    public static <T> GlobalTransformer<T> getWrapper(final boolean errorTips) {
        return getWrapper(errorTips, new RetryConfig(), true, false, "加载中···", null);
    }

    public static <T> GlobalTransformer<T> getWrapperWithLoading(Activity activity) {
        return getWrapper(true, new RetryConfig(), true, true, "加载中···", activity);
    }

    public static <T> GlobalTransformer<T> getWrapperWithLoading(Activity activity, String loadingTips) {
        return getWrapper(true, new RetryConfig(), true, true, loadingTips, activity);
    }

    /**
     * @param errorTips       code!=0 是是否自动打印错误msg
     * @param retryConfig     异常重试配置 无需重试传null
     * @param showLoading
     * @param showLoadingTips
     */
    public static <T> GlobalTransformer<T> getWrapper(final boolean errorTips, final RetryConfig retryConfig, final boolean catchException, boolean showLoading, String showLoadingTips, Activity activity) {    //compose简化线程
        return new GlobalTransformer<>(errorTips, retryConfig, catchException, showLoading, showLoadingTips, activity);
    }

    /**
     * 统一异常处理(aop)
     */
    public static <T> GlobalErrorTransformer<T> handleGlobalError(final boolean showErrorTips, final RetryConfig retryConfig) {
        return new GlobalErrorTransformer<>(
                //通过onNext流数据状态操作
                t -> {
                    //转为error交给下游
                    if (t instanceof BaseResult) {
                        BaseResult gResult = (BaseResult) t;
                        switch (gResult.getCode()) {
                            case NetworkCode.ERROR_SUCCESS://请求成功
                                break;
                            case NetworkCode.ERROR_UNLOGIN://未登录 or 被t出（token被销毁）
                                ToastUtils.showToast(NetworkCode.getMessage(String.valueOf(NetworkCode.ERROR_UNLOGIN)));
                                //EventBus.getDefault().post(EventCommand.kickOut());
                                break;
                            default:
                                if (showErrorTips) {
                                    ToastUtils.showToast(gResult.getMsg());
                                }
                                break;
                        }
                    }
                    return Observable.just(t);
                },

                //通过onError中Throwable状态操作
                throwable -> {
                    //error转为其他error交给下游
                    if (throwable instanceof HttpException) {
                    } else if (throwable instanceof JSONException) {
                    } else if (throwable instanceof ConnectException) {
                    } else if (throwable instanceof NumberFormatException) {
                    } else if (throwable instanceof javax.net.ssl.SSLHandshakeException) {
                    } else {
                    }
                    return Observable.error(throwable);
                },

                throwable -> {
                    //根据error决定是否重试
                    return retryConfig == null ? new RetryConfig() : retryConfig;
                },

                throwable -> {
                    //并不消费error,观察处理
                    ToastUtils.showToast("全局接口异常:" + throwable.getMessage());
                });
    }

    /**
     * 统一异常拦截
     */
    private static <T> ObservableTransformer<T, T> handleGlobalErrorCatch() {
        return upstream -> upstream.onExceptionResumeNext((ObservableSource<T>) observer -> {
            DLog.v(TAG, "=================== 拦截接口异常 ===================");
            observer.onComplete();
        });
    }


    /**
     * 配置参数Builder
     */
    public static class Builder {

        /**
         * 显示错误提示
         */
        private boolean mShowErrorTips;

        private RetryConfig mRetryConfig;

        /**
         * 拦截异常
         */
        private boolean mCatchException;

        private boolean mShowLoading;
        private String mShowLoadingTips;
        private Activity activity;

        public Builder() {
            mShowErrorTips = true;
            mRetryConfig = new RetryConfig();
            mCatchException = true;
            mShowLoading = false;
            mShowLoadingTips = "";
            activity = null;
        }

        /**
         * GResult code!=0 时是否显示服务器消息提示
         */
        public Builder showErrorTips(boolean errorTips) {
            mShowErrorTips = errorTips;
            return this;
        }

        /**
         * 开启重试
         */
        public Builder retry(int count, int delay) {
            mRetryConfig = new RetryConfig(count, delay, () -> Single.just(true));
            return this;
        }

        /**
         * 开启/关闭拦截异常
         * default: true
         */
        public Builder catchException(boolean exception) {
            mCatchException = exception;
            return this;
        }

        /**
         * 显示加载中对话框
         */
        public Builder showLoading(boolean showLoading) {
            mShowLoading = showLoading;
            return this;
        }

        /**
         * 显示加载中对话框
         */
        public Builder showLoading(String loadingTips) {
            mShowLoading = true;
            mShowLoadingTips = loadingTips;
            return this;
        }

        /**
         * 显示加载中对话框
         */
        public Builder showLoading(String loadingTips, Activity activity) {
            mShowLoading = true;
            mShowLoadingTips = loadingTips;
            this.activity = activity;
            return this;
        }

        public <T> ObservableTransformer<T, T> build() {
            return getWrapper(mShowErrorTips, mRetryConfig, mCatchException, mShowLoading, mShowLoadingTips, activity);
        }
    }
}

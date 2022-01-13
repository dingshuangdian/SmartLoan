package com.mmt.smartloan.rxjava.exception.core;

import android.app.Activity;

import com.mmt.smartloan.rxjava.exception.retry.RetryConfig;
import com.mmt.smartloan.utils.DLog;
import com.mmt.smartloan.utils.RxUtil;
import com.mmt.smartloan.view.LoadingDialog;

import org.reactivestreams.Publisher;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.CompletableTransformer;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Transformer 处理流程
 * 1.处理线程切换 io() -> mainThread()
 * 2.处理全局异常、错误提示、重试
 *
 * @param <T>
 */
public class GlobalTransformer<T> implements ObservableTransformer<T, T>,
        FlowableTransformer<T, T>,
        SingleTransformer<T, T>,
        MaybeTransformer<T, T>,
        CompletableTransformer {

    private static final String TAG = GlobalTransformer.class.getCanonicalName();

    //显示错误提示
    private boolean errorTips;
    //重试
    private RetryConfig retryConfig;
    //捕获异常
    private boolean catchException;
    //显示Loading框
    private boolean showLoading;
    //显示LoadingTips
    private String showLoadingTips;
    private Activity activity;
    private LoadingDialog loadingDialog;

    public GlobalTransformer(boolean errorTips, RetryConfig retryConfig, boolean catchException, boolean showLoading, String showLoadingTips, Activity activity) {
        this.errorTips = errorTips;
        this.retryConfig = retryConfig;
        this.catchException = catchException;
        this.showLoading = showLoading;
        this.showLoadingTips = showLoadingTips;
        this.activity = activity;
    }

    @Override
    public Publisher<T> apply(Flowable<T> upstream) {
        Flowable<T> observable = upstream
                .compose(upstream1 -> upstream1.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()))
                .compose(RxUtil.handleGlobalError(errorTips, retryConfig))
                .compose(upstream13 -> upstream13.doOnSubscribe(disposable -> {
                    if (showLoading) {
                        loadingDialog = new LoadingDialog(activity);
                        loadingDialog.setMessage(showLoadingTips);
                        loadingDialog.show();
                    }
                }).doFinally(() -> {
                    if (showLoading) {
                        loadingDialog.dismiss();
                    }
                }));
        if (catchException) {
            return observable.compose(upstream12 -> upstream12.onExceptionResumeNext(observer -> {
                DLog.v(TAG, "=================== 拦截接口异常1 ===================");
                observer.onComplete();
            }));
        } else {
            return observable;
        }
    }

    @Override
    public MaybeSource<T> apply(Maybe<T> upstream) {
        Maybe<T> observable = upstream
                .compose(upstream1 -> upstream1.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()))
                .compose(RxUtil.handleGlobalError(errorTips, retryConfig))
                .compose(upstream13 -> upstream13.doOnSubscribe(disposable -> {
                    if (showLoading) {
                        loadingDialog = new LoadingDialog(activity);
                        loadingDialog.setMessage(showLoadingTips);
                        loadingDialog.show();
                    }
                }).doFinally(() -> {
                    if (showLoading) {
                        loadingDialog.dismiss();
                    }
                }));
        if (catchException) {
            return observable.compose(upstream12 -> upstream12.onExceptionResumeNext(observer -> {
                DLog.v(TAG, "=================== 拦截接口异常2 ===================");
                observer.onComplete();
            }));
        } else {
            return observable;
        }
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        Observable<T> observable = upstream
                .compose(upstream1 -> upstream1.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()))
                .compose(RxUtil.handleGlobalError(errorTips, retryConfig))
                .compose(upstream13 -> upstream13.doOnSubscribe(disposable -> {
                    if (showLoading) {
                        loadingDialog = new LoadingDialog(activity);
                        loadingDialog.setMessage(showLoadingTips);
                        loadingDialog.show();
                    }
                }).doFinally(() -> {
                    if (loadingDialog != null&&loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                }));
        if (catchException) {
            return observable.compose(upstream12 -> upstream12.onExceptionResumeNext(observer -> {
                DLog.v(TAG, "=================== 拦截接口异常3 ===================");
                observer.onComplete();
            }));
        } else {
            return observable;
        }
    }

    @Override
    public SingleSource<T> apply(Single<T> upstream) {
        return upstream
                .compose(upstream1 -> upstream1.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()))
                .compose(RxUtil.handleGlobalError(errorTips, retryConfig))
                .compose(upstream13 -> upstream13.doOnSubscribe(disposable -> {
                    if (showLoading) {
                        loadingDialog = new LoadingDialog(activity);
                        loadingDialog.setMessage(showLoadingTips);
                        loadingDialog.show();
                    }
                }).doFinally(() -> {
                    if (showLoading) {
                        loadingDialog.dismiss();
                    }
                }));
    }

    @Override
    public CompletableSource apply(Completable upstream) {
        return upstream
                .compose(upstream1 -> upstream1.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()))
                .compose(RxUtil.handleGlobalError(errorTips, retryConfig))
                .compose(upstream13 -> upstream13.doOnSubscribe(disposable -> {
                    if (showLoading) {
                        loadingDialog = new LoadingDialog(activity);
                        loadingDialog.setMessage(showLoadingTips);
                        loadingDialog.show();
                    }
                }).doFinally(() -> {
                    if (showLoading) {
                        loadingDialog.dismiss();
                    }
                }));
    }
}
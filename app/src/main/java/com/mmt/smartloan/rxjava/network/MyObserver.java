package com.mmt.smartloan.rxjava.network;

import com.mmt.smartloan.base.BaseResult;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * <p>版权©️所有：大参林医药集团<p>
 * <p>作者：dingshuangdian<p>
 * <p>创建时间：2020/6/4<p>
 */
public abstract class MyObserver<T> implements Observer<BaseResult<T>> {
    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(BaseResult<T> tgResult) {
        if (tgResult.isSuccess()) {
            onResultSuccess(tgResult.getData());
        } else {
            onResultFailed(tgResult);
        }
        onResultFinish();
    }

    @Override
    public void onError(Throwable e) {
        onResultException(e);
        onResultFinish();
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        onResultFinish();
    }

    /**
     * 请求成功
     *
     * @param t
     */
    public abstract void onResultSuccess(T t);

    /**
     * 请求失败
     *
     * @param result
     */
    public void onResultFailed(BaseResult<T> result) {
    }

    /**
     * 完成（请求结束均会执行）
     */
    public void onResultFinish() {

    }

    /**
     * 异常（请求发生异常）
     *
     * @param e
     */
    public void onResultException(Throwable e) {
    }
}

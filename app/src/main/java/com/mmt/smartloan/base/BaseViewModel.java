package com.mmt.smartloan.base;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;


public class BaseViewModel<M> extends AndroidViewModel implements IBaseViewModel {
    protected M model;
    protected Activity activity;

    private BaseViewModel(@NonNull Application application) {
        super(application);
    }

    public BaseViewModel(M model, Activity activity) {
        this(activity.getApplication());
        this.model = model;
        this.activity = activity;
    }

    @Override
    public void onAny(LifecycleOwner owner, Lifecycle.Event event) {

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }
}

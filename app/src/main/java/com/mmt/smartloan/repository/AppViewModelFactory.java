package com.mmt.smartloan.repository;

import android.app.Activity;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.mmt.smartloan.module.LoginModule;

public class AppViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final Activity activity;
    private final Fragment fragment;
    private final RepositoryModule repositoryModule;

    public AppViewModelFactory(Activity activity, RepositoryModule repositoryModule) {
        this.activity = activity;
        this.fragment = null;
        this.repositoryModule = repositoryModule;
    }

    public AppViewModelFactory(Fragment fragment, RepositoryModule repositoryModule) {
        this.activity = fragment.getActivity();
        this.fragment = fragment;
        this.repositoryModule = repositoryModule;
    }

    public static AppViewModelFactory getInstance(Activity activity) {
        return new AppViewModelFactory(activity, Injection.provideRepository());
    }

    public static AppViewModelFactory getInstance(Fragment fragment) {
        return new AppViewModelFactory(fragment, Injection.provideRepository());
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginModule.class)) {

            return (T) new LoginModule(repositoryModule, activity);

        }

        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}

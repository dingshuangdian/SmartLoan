package com.mmt.smartloan.module;

import android.app.Activity;

import com.mmt.smartloan.base.BaseViewModel;
import com.mmt.smartloan.repository.RepositoryModule;

public class LoginModule extends BaseViewModel<RepositoryModule> {
    public LoginModule(RepositoryModule model, Activity activity) {
        super(model, activity);
    }
}

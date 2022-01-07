package com.mmt.smartloan.activity;

import androidx.lifecycle.ViewModelProviders;

import com.mmt.smartloan.BR;
import com.mmt.smartloan.R;
import com.mmt.smartloan.base.BaseActivity;
import com.mmt.smartloan.databinding.LoginLayoutBinding;
import com.mmt.smartloan.module.LoginModule;
import com.mmt.smartloan.repository.AppViewModelFactory;

public class LoginActivity extends BaseActivity<LoginLayoutBinding, LoginModule> {
    @Override
    public int getLayout() {
        return R.layout.login_layout;
    }

    @Override
    public int initVariableId() {
        return BR.login;
    }

    @Override
    public LoginModule initViewModel(AppViewModelFactory factory) {
        return ViewModelProviders.of(this, factory).get(LoginModule.class);
    }

    @Override
    public void initView() {

    }
}

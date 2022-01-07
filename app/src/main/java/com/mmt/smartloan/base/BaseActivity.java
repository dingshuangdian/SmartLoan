package com.mmt.smartloan.base;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import com.mmt.smartloan.repository.AppViewModelFactory;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class BaseActivity<V extends ViewDataBinding, VM extends BaseViewModel> extends FragmentActivity {
    protected V binding;
    private int viewModelId;
    protected VM viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViewDataBinding();
    }

    public V getBinding() {
        return binding;
    }

    private void initViewDataBinding() {
        binding = DataBindingUtil.setContentView(this, getLayout());
        viewModelId = initVariableId();
        viewModel = initViewModel(AppViewModelFactory.getInstance(this));
        if (viewModel == null) {
            Class modelClass;
            Type type = getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
            } else {
                //如果没有指定泛型参数，则默认使用BaseViewModel
                modelClass = BaseViewModel.class;
            }
            viewModel = (VM) createViewModel(this, modelClass);
        }
        //关联ViewModel
        binding.setVariable(viewModelId, viewModel);
        initView();
        //让ViewModel拥有View的生命周期感应
        getLifecycle().addObserver(viewModel);
        ActivityStackManager.getInstance().addActivity(this);

    }

    public abstract int getLayout();

    public abstract int initVariableId();

    public abstract VM initViewModel(AppViewModelFactory factory);

    public abstract void initView();

    public <T extends ViewModel> T createViewModel(FragmentActivity activity, Class<T> cls) {
        return ViewModelProviders.of(activity).get(cls);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ActivityStackManager.getInstance().removeActivity(this);


    }

    public void backToPreActivity(View view) {
        this.finish();
    }
}



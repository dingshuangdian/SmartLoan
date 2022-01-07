package com.mmt.smartloan.repository;


import com.mmt.smartloan.base.BaseInterface;
import com.mmt.smartloan.rxjava.network.NetworkFactory;

public class Injection {
    public static RepositoryModule provideRepository() {
        return RepositoryModule.getINSTANCE(NetworkFactory.getInterface(BaseInterface.class));
    }
}

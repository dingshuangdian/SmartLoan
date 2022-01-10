package com.mmt.smartloan.repository;

import androidx.annotation.NonNull;

import com.mmt.smartloan.base.BaseInterface;
import com.mmt.smartloan.base.BaseResult;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

/**
 * <p>版权©️所有：大参林医药集团<p>
 * <p>作者：dingshuangdian<p>
 * <p>创建时间：2020/6/4<p>
 */
public class RepositoryModule implements BaseInterface {
    private volatile static RepositoryModule INSTANCE = null;
    private BaseInterface baseInterface;

    public RepositoryModule(@NonNull BaseInterface baseInterface) {
        this.baseInterface = baseInterface;
    }

    public static RepositoryModule getINSTANCE(BaseInterface dslInterface) {
        if (INSTANCE == null) {
            synchronized (RepositoryModule.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RepositoryModule(dslInterface);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public Observable<BaseResult<Object>> setActivePush(Map<String, Object> map) {
        return baseInterface.setActivePush(map);
    }
}

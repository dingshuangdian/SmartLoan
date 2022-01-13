package com.mmt.smartloan.repository;

import androidx.annotation.NonNull;

import com.mmt.smartloan.base.BaseInterface;
import com.mmt.smartloan.base.BaseResult;
import com.mmt.smartloan.bean.ExistsMobileBean;
import com.mmt.smartloan.bean.RegisterAndLoginBean;
import com.mmt.smartloan.bean.VerifyCodeBean;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

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
    public Observable<BaseResult<Object>> setActivePush(RequestBody body) {
        return baseInterface.setActivePush(body);
    }

    @Override
    public Observable<BaseResult<ExistsMobileBean>> existsByMobile(Map<String, Object> map) {
        return baseInterface.existsByMobile(map);
    }

    @Override
    public Observable<BaseResult<VerifyCodeBean>> getVerifyCode(RequestBody body) {
        return baseInterface.getVerifyCode(body);
    }

    @Override
    public Observable<BaseResult<RegisterAndLoginBean>> registerSys(RequestBody body) {
        return baseInterface.registerSys(body);
    }

    @Override
    public Observable<BaseResult<RegisterAndLoginBean>> loginSys(Map<String, Object> map) {
        return baseInterface.loginSys(map);
    }

    @Override
    public Observable<BaseResult<Object>> zip6in1(RequestBody md5, RequestBody orderNo, MultipartBody.Part file) {
        return baseInterface.zip6in1(md5, orderNo, file);
    }


}

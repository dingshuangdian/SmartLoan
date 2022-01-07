package com.mmt.smartloan.base;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface BaseInterface {
    /**
     * 发送短信验证码
     *
     * @param map
     * @return
     */
    @GET("/app/sms/send.do")
    Observable<BaseResult<Object>> sendSms(@QueryMap Map<String, Object> map);

}

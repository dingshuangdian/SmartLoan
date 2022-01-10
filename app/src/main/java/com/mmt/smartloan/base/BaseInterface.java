package com.mmt.smartloan.base;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface BaseInterface {

    /**
     * @param map
     * @return
     */
    @POST("/user/device/addActive")
    Observable<BaseResult<Object>> setActivePush(@QueryMap Map<String, Object> map);

}

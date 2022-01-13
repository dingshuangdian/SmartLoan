package com.mmt.smartloan.base;

import com.mmt.smartloan.bean.ExistsMobileBean;
import com.mmt.smartloan.bean.RegisterAndLoginBean;
import com.mmt.smartloan.bean.VerifyCodeBean;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;

public interface BaseInterface {

    /**
     * @param body
     * @return
     */
    @POST("user/device/addActive")
    Observable<BaseResult<Object>> setActivePush(@Body RequestBody body);

    @GET("security/existsByMobile")
    Observable<BaseResult<ExistsMobileBean>> existsByMobile(@QueryMap Map<String, Object> map);

    @POST("security/getVerifyCode")
    Observable<BaseResult<VerifyCodeBean>> getVerifyCode(@Body RequestBody body);

    @POST("security/register")
    Observable<BaseResult<RegisterAndLoginBean>> registerSys(@Body RequestBody body);

    @FormUrlEncoded
    @POST("security/login")
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    Observable<BaseResult<RegisterAndLoginBean>> loginSys(@FieldMap Map<String, Object> params);

    @Multipart
    @POST("time/upload/zip6in1")
    Observable<BaseResult<Object>> zip6in1(@Part("md5") RequestBody md5,@Part("orderNo") RequestBody orderNo, @Part MultipartBody.Part file);
}

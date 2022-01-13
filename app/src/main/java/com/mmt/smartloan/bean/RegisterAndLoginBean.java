package com.mmt.smartloan.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <p>版权©️所有：大参林医药集团<p>
 * <p>作者：dingshuangdian<p>
 * <p>创建时间：2022/1/11<p>
 */
public class RegisterAndLoginBean implements Parcelable {


    private String token;
    private String userId;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.token);
        dest.writeString(this.userId);
    }

    public void readFromParcel(Parcel source) {
        this.token = source.readString();
        this.userId = source.readString();
    }

    public RegisterAndLoginBean() {
    }

    protected RegisterAndLoginBean(Parcel in) {
        this.token = in.readString();
        this.userId = in.readString();
    }

    public static final Parcelable.Creator<RegisterAndLoginBean> CREATOR = new Parcelable.Creator<RegisterAndLoginBean>() {
        @Override
        public RegisterAndLoginBean createFromParcel(Parcel source) {
            return new RegisterAndLoginBean(source);
        }

        @Override
        public RegisterAndLoginBean[] newArray(int size) {
            return new RegisterAndLoginBean[size];
        }
    };
}

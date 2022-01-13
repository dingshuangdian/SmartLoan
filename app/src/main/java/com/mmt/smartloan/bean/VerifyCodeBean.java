package com.mmt.smartloan.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <p>版权©️所有：大参林医药集团<p>
 * <p>作者：dingshuangdian<p>
 * <p>创建时间：2022/1/11<p>
 */
public class VerifyCodeBean implements Parcelable {



    private String code;
    private boolean enableAutoLogin;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isEnableAutoLogin() {
        return enableAutoLogin;
    }

    public void setEnableAutoLogin(boolean enableAutoLogin) {
        this.enableAutoLogin = enableAutoLogin;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.code);
        dest.writeByte(this.enableAutoLogin ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.code = source.readString();
        this.enableAutoLogin = source.readByte() != 0;
    }

    public VerifyCodeBean() {
    }

    protected VerifyCodeBean(Parcel in) {
        this.code = in.readString();
        this.enableAutoLogin = in.readByte() != 0;
    }

    public static final Parcelable.Creator<VerifyCodeBean> CREATOR = new Parcelable.Creator<VerifyCodeBean>() {
        @Override
        public VerifyCodeBean createFromParcel(Parcel source) {
            return new VerifyCodeBean(source);
        }

        @Override
        public VerifyCodeBean[] newArray(int size) {
            return new VerifyCodeBean[size];
        }
    };
}

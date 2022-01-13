package com.mmt.smartloan.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <p>版权©️所有：大参林医药集团<p>
 * <p>作者：dingshuangdian<p>
 * <p>创建时间：2022/1/11<p>
 */
public class ExistsMobileBean implements Parcelable {



    private String mobile;
    private boolean existed;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean isExisted() {
        return existed;
    }

    public void setExisted(boolean existed) {
        this.existed = existed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mobile);
        dest.writeByte(this.existed ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.mobile = source.readString();
        this.existed = source.readByte() != 0;
    }

    public ExistsMobileBean() {
    }

    protected ExistsMobileBean(Parcel in) {
        this.mobile = in.readString();
        this.existed = in.readByte() != 0;
    }

    public static final Parcelable.Creator<ExistsMobileBean> CREATOR = new Parcelable.Creator<ExistsMobileBean>() {
        @Override
        public ExistsMobileBean createFromParcel(Parcel source) {
            return new ExistsMobileBean(source);
        }

        @Override
        public ExistsMobileBean[] newArray(int size) {
            return new ExistsMobileBean[size];
        }
    };
}

package com.mmt.smartloan.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class UpdateInfoBean implements Parcelable {


    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    private String link;


    public boolean isForcedUpdate() {
        return forcedUpdate;
    }

    public void setForcedUpdate(boolean forcedUpdate) {
        this.forcedUpdate = forcedUpdate;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    private boolean forcedUpdate;
    private String versionCode;
    private String versionName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.link);
        dest.writeByte(this.forcedUpdate ? (byte) 1 : (byte) 0);
        dest.writeString(this.versionCode);
        dest.writeString(this.versionName);
    }

    public void readFromParcel(Parcel source) {
        this.link = source.readString();
        this.forcedUpdate = source.readByte() != 0;
        this.versionCode = source.readString();
        this.versionName = source.readString();
    }

    public UpdateInfoBean() {
    }

    protected UpdateInfoBean(Parcel in) {
        this.link = in.readString();
        this.forcedUpdate = in.readByte() != 0;
        this.versionCode = in.readString();
        this.versionName = in.readString();
    }

    public static final Creator<UpdateInfoBean> CREATOR = new Creator<UpdateInfoBean>() {
        @Override
        public UpdateInfoBean createFromParcel(Parcel source) {
            return new UpdateInfoBean(source);
        }

        @Override
        public UpdateInfoBean[] newArray(int size) {
            return new UpdateInfoBean[size];
        }
    };
}

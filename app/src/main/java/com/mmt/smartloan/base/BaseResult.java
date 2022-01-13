package com.mmt.smartloan.base;

import java.io.Serializable;

public class BaseResult<T> implements Serializable {
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    protected int code;
    protected String msg;
    protected T data;

    public boolean isSuccess() {
        return code == 0;
    }


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


}

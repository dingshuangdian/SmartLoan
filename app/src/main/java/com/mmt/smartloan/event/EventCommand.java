package com.mmt.smartloan.event;

import java.util.HashMap;

/**
 * <p>版权©️所有：大参林医药集团<p>
 * <p>作者：dingshuangdian<p>
 * <p>创建时间：2020/6/4<p>
 */
public class EventCommand<T> {
    public static final int CODE_LOGIN_NORMAL = 0;//正常退出
    public static final int CODE_LOGIN_KICK_OUT = 1;//被T出
    public static final int REQESTPERMISSIONS = 2;//权限

    public static class sys {
        public static final String LOGOUT = "GC_SYSTEM_LOGOUT";//登出
        public static final String LOGIN = "GC_SYSTEM_LOGIN";//登入
        public static final String PERMISSIONS = "REQESTPERMISSIONS";//权限

    }

    public EventCommand() {
    }
    /**
     * 登出事件（用户正常退出）
     */
    public static EventCommand logout() {
        return newInstance(sys.LOGOUT, CODE_LOGIN_NORMAL);
    }

    /**
     * 登出事件（被T出）
     *
     * @return
     */
    public static EventCommand kickOut() {
        return newInstance(sys.LOGOUT, CODE_LOGIN_KICK_OUT);
    }

    /**
     * 登入事件
     */
    public static EventCommand login(Object params) {
        return newInstance(sys.LOGIN, params);
    }



    public static EventCommand permission() {
        return newInstance(sys.PERMISSIONS, REQESTPERMISSIONS);
    }

    public static EventCommand newInstance(String comment) {
        EventCommand commend = new EventCommand();
        commend.commend = comment;
        commend.param = new HashMap<>();
        return commend;
    }

    public static EventCommand newInstance(String comment, Object data) {
        EventCommand commend = new EventCommand();
        commend.commend = comment;
        commend.param = new HashMap<>();
        commend.param.put("event_key_value", data);
        return commend;
    }

    public String commend;
    private int code;
    public HashMap<String, Object> param = new HashMap<>();

    public Object getParamData() {
        return param.get("event_key_value");
    }

    public Object getParam(String key) {
        return param.get(key);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public EventCommand addParam(String key, Object val) {
        param.put(key, val);
        return this;
    }

}

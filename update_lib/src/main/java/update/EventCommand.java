package update;

import java.util.HashMap;

/**
 * <p>版权©️所有：大参林医药集团<p>
 * <p>作者：dingshuangdian<p>
 * <p>创建时间：2020/6/4<p>
 */
public class EventCommand<T> {
    public static final int CODE_MSG = 0;

    public static class sys {
        public static final String SENDMSG = "SEND_MSG";//发送消息
        public static final String SENDTYPE = "SEND_TYPE";//发送消息
        public static final String SENDNETWORK = "SEND_NETWORK";//发送网络状态消息
    }

    public EventCommand() {
    }


    /**
     * 发送消息
     */
    public static EventCommand senMsg(Object o) {
        return newInstance(sys.SENDMSG, o);
    }

    /**
     * 发送网络状态消息
     */
    public static EventCommand senNetWorkMsg(Object o) {
        return newInstance(sys.SENDNETWORK, o);
    }

    /**
     * 发送消息
     */
    public static EventCommand senMsg(String name, Object o) {
        return newInstance(name, o);
    }

    /**
     * 发送类型
     */
    public static EventCommand sendType(Object o) {
        return newInstance(sys.SENDTYPE, o);
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

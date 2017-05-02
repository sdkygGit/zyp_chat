package com.skysea.group.packet.notify;

/**
 * 通知基类。
 * Created by zhangzhi on 2014/9/23.
 */
public abstract class Notify {
    private final Type type;

    protected Notify(Type type){
        if(type == null) { throw new NullPointerException("type"); }
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        MEMBER_JOINED("join"),
        MEMBER_EXITED("exit"),
        MEMBER_KICKED("kick"),
        MEMBER_INVITE("invite"),
        MEMBER_PROFILE_CHANGED("profile"),
        MEMBER_APPLY_TO_JOIN("apply"),
        MEMBER_APPLY_TO_JOIN_RESULT("apply"),
        GROUP_CHANGE("change"),
        GROUP_DESTROY("destroy");

        private final String name;
        Type(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }
}

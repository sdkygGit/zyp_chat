package com.skysea.group.packet.notify;

import com.skysea.group.packet.HasReason;

/**
 * 圈子销毁的通知。
 * Created by zhangzhi on 2014/9/23.
 */
public final class GroupDestroyedNotify extends Notify implements HasOperator , HasReason {
    private String from;
    private String reason;

    public GroupDestroyedNotify() {
        super(Type.GROUP_DESTROY);
    }

    @Override
    public String getFrom() {
        return from;
    }

    @Override
    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public String getReason() {
        return reason;
    }

    @Override
    public void setReason(String reason) {
        this.reason = reason;
    }
}

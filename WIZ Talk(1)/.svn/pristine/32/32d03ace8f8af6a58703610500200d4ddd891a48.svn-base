package com.skysea.group.packet.notify;

import com.skysea.group.packet.HasReason;

/**
 * 成员退出圈子通知。
 * Created by apple on 14/11/2.
 */
public final class MemberExitedNotify extends HasMemberNotify implements HasReason {
    private String reason;

    public MemberExitedNotify() {
        super(Type.MEMBER_EXITED);
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

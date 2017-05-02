package com.skysea.group.packet.notify;

import com.skysea.group.packet.HasReason;

/**
 * 申请加入圈子的通知。
 * Created by zhangzhi on 2014/9/23.
 */
public final class MemberApplyToJoinNotify extends HasMemberNotify implements HasReason {
    private String id;
    private String reason;

    public MemberApplyToJoinNotify() {
        super(Type.MEMBER_APPLY_TO_JOIN);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

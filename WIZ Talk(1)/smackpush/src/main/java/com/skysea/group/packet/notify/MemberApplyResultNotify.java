package com.skysea.group.packet.notify;

import com.skysea.group.packet.HasReason;

/**
 * 申请处理结果通知。
 * Created by apple on 14-9-23.
 */
public final class MemberApplyResultNotify extends Notify implements HasReason, HasOperator {
    private boolean result;
    private String reason;
    private String from;

    public MemberApplyResultNotify() {
        super(Type.MEMBER_APPLY_TO_JOIN_RESULT);
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
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

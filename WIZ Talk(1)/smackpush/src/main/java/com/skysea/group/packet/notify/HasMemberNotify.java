package com.skysea.group.packet.notify;

import com.skysea.group.MemberInfo;
import com.skysea.group.packet.HasMember;

/**
 * 包含成员信息的通知。
 * Created by apple on 14/11/2.
 */
public abstract class HasMemberNotify extends Notify implements HasMember {
    private MemberInfo memberInfo;
    protected HasMemberNotify(Type type) {
        super(type);
    }

    public MemberInfo getMemberInfo() {
        return memberInfo;
    }

    public void setMemberInfo(MemberInfo memberInfo) {
        this.memberInfo = memberInfo;
    }

}
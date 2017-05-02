package com.skysea.group.packet.notify;

import com.skysea.group.MemberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 成员被邀请的通知。
 * Created by zhangzhi on 2014/9/23.
 */
public final class MemberInviteNotify extends Notify implements HasOperator {
    private String from;
    private ArrayList<MemberInfo> members = new ArrayList<MemberInfo>();

    public MemberInviteNotify() {
        super(Type.MEMBER_INVITE);
    }

    @Override
    public String getFrom() {
        return from;
    }

    @Override
    public void setFrom(String from) {
        this.from = from;
    }

    protected void addMember(MemberInfo member) {
        members.add(member);
    }

    public List<MemberInfo> getMembers() {
        return members;
    }

}

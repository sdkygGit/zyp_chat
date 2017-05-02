package com.skysea.group.packet.notify;

/**
 * 用户修改圈子名片的通知。
 * Created by zhangzhi on 2014/9/23.
 */
public final class MemberProfileChangedNotify extends HasMemberNotify {
    private String newNickname;
    public MemberProfileChangedNotify() {
        super(Type.MEMBER_PROFILE_CHANGED);
    }

    public String getNewNickname() {
        return newNickname;
    }

    public void setNewNickname(String newNickname) {
        this.newNickname = newNickname;
    }
}

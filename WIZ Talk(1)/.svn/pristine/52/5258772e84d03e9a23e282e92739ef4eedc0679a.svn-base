package com.skysea.group.packet.operate;

import com.skysea.group.MemberInfo;
import com.skysea.group.packet.HasMember;
import com.skysea.group.packet.HasReason;
import org.jivesoftware.smack.util.XmlStringBuilder;

/**
 * 处理圈子邀请的操作。
 * Created by zhangzhi on 2014/9/23.
 */
public final class ProcessInviteOperate extends Operate implements HasMember, HasReason {
    private final boolean result;
    private String reason;
    private MemberInfo memberInfo;

    public ProcessInviteOperate( String username, String nickname, boolean result) {
        super("invite");
        this.memberInfo = new MemberInfo(username, nickname);
        this.result = result;
    }


    public boolean getResult() {
        return result;
    }


    @Override
    protected void startElement(XmlStringBuilder builder) {
        super.startElement(builder);
    }

    @Override
    public MemberInfo getMemberInfo() {
        return memberInfo;
    }

    @Override
    public void setMemberInfo(MemberInfo member) {
        this.memberInfo = member;
    }

    @Override
    public String getReason() {
        return reason;
    }

    @Override
    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    protected void childrenElements(XmlStringBuilder builder) {
        builder.halfOpenElement(result ? "agree" : "decline")
                .closeEmptyElement()
                .halfOpenElement("member")
                .attribute("username", memberInfo.getUserName())
                .optAttribute("nickname", memberInfo.getNickname())
                .closeEmptyElement()
                .optElement("reason", reason);
        super.childrenElements(builder);
    }
}



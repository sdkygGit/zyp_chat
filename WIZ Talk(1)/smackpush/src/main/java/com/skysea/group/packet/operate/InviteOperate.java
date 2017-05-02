package com.skysea.group.packet.operate;

import com.skysea.group.MemberInfo;
import org.jivesoftware.smack.util.XmlStringBuilder;

import java.util.ArrayList;

/**
 * 邀请操作。
 * <invite>
 * <member username='user100' nickname='独孤求败' />
 * </invite>
 * Created by zhangzhi on 2014/10/9.
 */
public final class InviteOperate extends Operate {
    private ArrayList<MemberInfo> members = new ArrayList<MemberInfo>();

    public InviteOperate() {
        super("invite");
    }

    public void addMember(String userName, String nickname) {
        members.add(new MemberInfo(userName, nickname));
    }

    @Override
    protected void childrenElements(XmlStringBuilder builder) {
        for(MemberInfo member:members) {
            builder.halfOpenElement("member")
                    .attribute("username", member.getUserName())
                    .optAttribute("nickname", member.getNickname())
                    .closeEmptyElement();
        }
    }



}

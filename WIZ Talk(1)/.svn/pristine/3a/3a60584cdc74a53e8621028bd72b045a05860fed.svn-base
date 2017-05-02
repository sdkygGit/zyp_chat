package com.skysea.group.packet.operate;

import org.jivesoftware.smack.util.XmlStringBuilder;

/**
 * 修改圈子名片的操作。
 *  <profile>
 *      <nickname>金轮法王</nickname>
 *  </profile>
 * Created by zhangzhi on 2014/9/23.
 */
public final class ChangeProfileOperate extends Operate {
    private String nickname;

    public ChangeProfileOperate() {
        super("profile");
    }

    /**
     * 获得要设置的新用户昵称。
     * @return
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * 设置新的用户昵称。
     * @param nickname 新的用户昵称。
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    protected void childrenElements(XmlStringBuilder builder) {
        builder.element("nickname", nickname);
    }
}

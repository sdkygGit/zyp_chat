package com.skysea.group;

import org.jivesoftware.smack.packet.Message;

/**
 * Created by zhangzhi on 2014/9/22.
 */
public class MemberInfo {
    private final String nickname;
    private final String userName;

    public MemberInfo(String userName, String nickname){
        this.userName = userName;
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    /**
     * 
     * @return  8acsskjalskjflkasjkj  不带@user
     */
    public String getUserName() {
        return userName;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (nickname != null ? nickname.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) { return  false; }
        if(!(o instanceof MemberInfo)) { return false; }

        MemberInfo other = (MemberInfo)o;
        return (userName == null
                    ? other.userName == null
                    : userName.equals(other.userName)) &&
                (nickname == null
                    ? other.nickname == null
                    : nickname.equals(other.nickname));
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", userName, nickname);
    }

}

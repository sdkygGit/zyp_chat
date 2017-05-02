package com.skysea.group.packet.operate;

import com.skysea.group.packet.HasReason;
import org.jivesoftware.smack.util.XmlStringBuilder;

/**
 * 踢人操作。
 *
 *  <kick username='user'>
 *      <reason>抱歉！你总是发送广告信息。</reason>
 *  </kick>
 *  
 * Created by zhangzhi on 2014/9/23.
 */
public final class KickOperate extends Operate implements HasReason {
    private final String userName;
    private String reason;

    /**
     * @param userName 被踢用户名。
     */
    public KickOperate(String userName) {
        super("kick");
        this.userName = userName;
    }

    /**
     * 获得被踢的用户名。
     * @return
     */
    public String getUserName() {
        return userName;
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
    protected void startElement(XmlStringBuilder builder) {
        super.startElement(builder);
        builder.attribute("username", userName);
    }

    @Override
    protected void childrenElements(XmlStringBuilder builder) {
        builder.optElement("reason", getReason());
    }

}

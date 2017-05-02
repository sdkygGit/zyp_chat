package com.skysea.group.packet.operate;

import com.skysea.group.packet.HasReason;
import org.jivesoftware.smack.util.XmlStringBuilder;

/**
 * 申请加入圈子的操作
 *
 * <apply>
 *      <member nickname='碧眼狐狸' />
 *      <reason>我也是80后，请让我加入吧！</reason>
 *  </apply>
 *
 * Created by apple on 14-9-30.
 */
public final class ApplyOperate extends Operate implements HasReason {
    private String nickName;
    private String reason;

    public ApplyOperate() {
        super("apply");
    }

    public void setNickname(String nickName) {
        this.nickName = nickName;
    }

    @Override
    protected void childrenElements(XmlStringBuilder builder) {
        if (nickName != null) {
            builder.halfOpenElement("member")
                    .attribute("nickname", nickName)
                    .closeEmptyElement();
        }
        builder.optElement("reason", reason);
        super.childrenElements(builder);
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

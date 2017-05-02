package com.skysea.group.packet.operate;

import com.skysea.group.packet.HasReason;
import org.jivesoftware.smack.util.XmlStringBuilder;

/**
 * 包含原因的操作。
 * Created by zhangzhi on 2014/9/23.
 */
public final class HasReasonOperate extends Operate implements HasReason {
    private String reason;
    private HasReasonOperate(String type, String reason) {
        super(type);
        this.reason = reason;
    }

    /**
     * 获得操作原因。
     * @return
     */
    @Override
    public String getReason() {
        return reason;
    }

    /**
     * 设置操作原因。
     * @param reason
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    protected void childrenElements(XmlStringBuilder builder) {
        builder.optElement("reason", getReason());
    }


    /**
     * 创建一个退出圈子的操作对象。
     *  <exit>
     *      <reason>大家太吵了，不好意思，我退了先！</reason>
     *  </exit>
     * @param reason
     * @return
     */
    public static HasReasonOperate newInstanceForExitGroup(String reason) {
        return new HasReasonOperate("exit", reason);
    }

    /**
     * 创建一个销毁圈子的操作对象。
     *
     *  <destroy>
     *      <reason>再见了各位！</reason>
     *  </destroy>
     * @param reason
     * @return
     */
    public static HasReasonOperate newInstanceForDestroyGroup(String reason) {
        return new HasReasonOperate("destroy", reason);
    }
}

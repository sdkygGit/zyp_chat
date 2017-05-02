package com.skysea.group.packet.notify;

/**
 * 圈子信息修改通知。
 * Created by zhangzhi on 2014/9/23.
 */
public final class GroupChangedNotify extends Notify implements HasOperator{
    private String from;

    public GroupChangedNotify() {
        super(Type.GROUP_CHANGE);
    }

    @Override
    public String getFrom() {
        return from;
    }

    @Override
    public void setFrom(String from) {
        this.from = from;
    }

}

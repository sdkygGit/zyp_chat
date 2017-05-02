package com.skysea.group;

import org.jivesoftware.smackx.xdata.packet.DataForm;

/**
 * 圈子事件监听器。
 * Created by zhangzhi on 2014/9/22.
 */
public interface GroupEventListener {

    /**
     * 当圈子已被创建。
     * @param groupJid 新创建的圈子jid。
     * @param createForm 圈子创建表单。
     */
    void created(String groupJid, DataForm createForm);

    /**
     * 当圈子已被销毁。
     * @param groupJid 被销毁的圈子jid。
     * @param from 操作人jid。
     * @param reason 销毁原因。
     */
    void destroyed(String groupJid, String from, String reason);

    /**
     * 新成员加入事件。
     * @param groupJid 圈子jid。
     * @param member 加入的成员。
     */
    void memberJoined(String groupJid, MemberInfo member);

    /**
     * 圈子成员退出事件。
     * @param groupJid 圈子jid。
     * @param member 退出的成员。
     */
    void memberExited(String groupJid, MemberInfo member, String reason);

    /**
     * 圈子成员被踢出事件。
     * @param groupJid 圈子jid。
     * @param member 踢出的成员。
     * @param from 操作人jid。
     * @param reason 原因。
     */
    void memberKicked(String groupJid, MemberInfo member, String from, String reason);

    /**
     * 昵称修改事件。
     * @param groupJid 圈子jid。
     * @param member 修改的成员。
     * @param newNickname 新的昵称。
     */
    void memberNicknameChanged(String groupJid, MemberInfo member, String newNickname);

    /**
     * 当新的用户申请到达。
     * @param groupJid 申请加入的圈子jid。
     * @param id 申请事务id。
     * @param member 成员信息。
     * @param reason 申请验证消息。
     */
    void applyArrived(String groupJid, String id, MemberInfo member, String reason);

    /**
     * 当申请已经被处理。
     * @param groupJid 申请加入的圈子jid。
     * @param agree 处理人是否同意加入。
     * @param from 处理人jid。
     * @param reason 处理人附言。
     */
    void applyProcessed(String groupJid, boolean agree, String from, String reason);
}

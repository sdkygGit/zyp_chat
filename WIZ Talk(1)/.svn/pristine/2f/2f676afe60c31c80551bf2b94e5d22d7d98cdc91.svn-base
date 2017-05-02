package com.skysea.group.packet;

import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.xdata.packet.DataForm;

/**
 * 圈子搜索请求包。
 * Created by zhangzhi on 2014/9/18.
 */
public class GroupSearch extends QueryPacket {
    private RSMPacket rsm;

    public GroupSearch() {
        super("jabber:iq:search");
    }

    public GroupSearch(DataForm dataForm, RSMPacket rsm){
        this();
        this.dataForm = dataForm;
        this.rsm = rsm;
    }

    public GroupSearch(UserSearch packet) {
        this();
        this.dataForm = packet.getExtension(DataForm.ELEMENT, DataForm.NAMESPACE);
        this.rsm = RSMPacket.getRSMFrom(packet);
    }

    @Override
    protected void childrenElements(XmlStringBuilder builder) {
        super.childrenElements(builder);
        if(rsm != null) {
            builder.append(rsm.toXML());
        }
    }

    public RSMPacket getRsm() {
        return rsm;
    }

    public void setRsm(RSMPacket rsm) {
        this.rsm = rsm;
    }
}

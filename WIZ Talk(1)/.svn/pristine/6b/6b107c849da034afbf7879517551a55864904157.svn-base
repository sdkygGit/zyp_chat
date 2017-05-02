package com.skysea.group.packet;

import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.xdata.packet.DataForm;

/**
 * 数据表单包。
 * Created by zhangzhi on 2014/9/18.
 */
public abstract class DataFormPacket extends GroupPacket {
    protected DataForm dataForm;

    protected DataFormPacket(String elementName, String namespace) {
        super(namespace, elementName);
    }

    @Override
    protected void childrenElements(XmlStringBuilder builder){
        if(dataForm != null) {
            builder.append(dataForm.toXML());
        }
    }

    public DataForm getDataForm() {
        if (dataForm == null) {
            dataForm = (DataForm) getExtension("x", "jabber:x:data");
        }
        return dataForm;
    }

    public void setDataForm(DataForm dataForm) {
        this.dataForm = dataForm;
    }

}

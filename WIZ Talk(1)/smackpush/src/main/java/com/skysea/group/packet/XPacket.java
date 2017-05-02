package com.skysea.group.packet;

import com.skysea.group.packet.notify.Notify;
import com.skysea.group.packet.operate.Operate;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.xdata.packet.DataForm;

/**
 * X请求包。
 * Created by zhangzhi on 2014/9/18.
 */
public class XPacket extends DataFormPacket {
    private Operate operate;
    public XPacket(String namespace) {
        super("x", namespace);
    }

    public XPacket(String namespace, DataForm form) {
        this(namespace);
        this.dataForm = form;
    }

    public XPacket(String namespace, Operate operate) {
        this(namespace);
        this.operate = operate;
    }

    public Operate getOperate() {
        return operate;
    }

    public void setOperate(Operate operate) {
        this.operate = operate;
    }

    @Override
    protected void childrenElements(XmlStringBuilder builder) {
        super.childrenElements(builder);
        if(operate != null) {
            builder.append(operate.toXML());
        }
    }


}

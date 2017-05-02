package com.skysea.group.packet;

import com.skysea.group.packet.notify.Notify;
import org.jivesoftware.smack.packet.PacketExtension;

/**
 * Created by apple on 14-9-24.
 */
public class NotifyPacketExtension implements PacketExtension {
    private final String elementName;
    private final String namespace;
    private Notify notify;
    public NotifyPacketExtension(String elementName, String namespace) {
        this.elementName = elementName;
        this.namespace = namespace;
    }

    @Override
    public String getElementName() {
        return elementName;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public CharSequence toXML() {
        return "";
    }

    public Notify getNotify() {
        return notify;
    }

    public void setNotify(Notify notify) {
        this.notify = notify;
    }
}

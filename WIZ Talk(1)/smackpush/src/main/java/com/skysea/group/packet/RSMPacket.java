package com.skysea.group.packet;

import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.util.XmlStringBuilder;


/**
 * XEP-0059 不完全封装
 * Created by zhangzhi on 2014/9/17.
 */
public class RSMPacket implements PacketExtension {
    private final static int UNDEFINED      = -1;
    public static final String ELEMENT = "set";
    public static final String NAMESPACE    = "http://jabber.org/protocol/rsm";

    private int max     = UNDEFINED;
    private int index   = UNDEFINED;
    private int count   = UNDEFINED;
    private String first;
    private String last;

    @Override
    public String getElementName() {
        return ELEMENT;
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public CharSequence toXML() {
        XmlStringBuilder builder = new XmlStringBuilder()
                .halfOpenElement(getElementName())
                .xmlnsAttribute(getNamespace())
                .rightAngelBracket();

        if(isSet(max)) {
            builder.element("max", String.valueOf(max));
        }

        if(isSet(index)) {
            builder.element("index", String.valueOf(index));
        }

        return builder.closeElement(getElementName()).toString();
    }

    private boolean isSet(int max) {
        return max != UNDEFINED;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public static RSMPacket getRSMFrom(Packet packet) {
        if(packet == null){ throw  new NullPointerException("packet"); }
        return packet.getExtension(ELEMENT, NAMESPACE);
    }
}

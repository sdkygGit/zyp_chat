package com.skysea.group.packet.operate;

import org.jivesoftware.smack.util.XmlStringBuilder;

/**
 * 抽象操作基类。
 * Created by zhangzhi on 2014/9/23.
 */
public abstract class Operate {
    private final String type;

    protected Operate(String type) {
        if(type == null || type.length() == 0) {
            throw  new IllegalArgumentException("type is invalid.");
        }
        this.type = type;
    }

    /**
     * 获得当前操作类型。
     * @return
     */
    public String getType() {
        return type;
    }


    /**
     * 获得操作的xml数据。
     * @return
     */
    public XmlStringBuilder toXML() {
        XmlStringBuilder builder = new XmlStringBuilder();
        startElement(builder);
        builder.rightAngelBracket();

        childrenElements(builder);
        closeElement(builder);
        return builder;
    }


    protected void startElement(XmlStringBuilder builder)
    {
        builder.halfOpenElement(type.toString());
    }

    protected  void childrenElements(XmlStringBuilder builder){

    }

    protected void closeElement(XmlStringBuilder builder) {
        builder.closeElement(type.toString());
    }

}

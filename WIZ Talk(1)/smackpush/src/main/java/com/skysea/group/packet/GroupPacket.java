package com.skysea.group.packet;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.util.XmlStringBuilder;

/**
 * Created by zhangzhi on 2014/9/22.
 */
public abstract class GroupPacket extends IQ {
    protected final String elementName;
    protected String namespace;
    
    public GroupPacket(String namespace, String elementName) {
    	super(elementName, namespace);
        this.namespace = namespace;
        this.elementName = elementName;

        if(elementName == null){ throw new NullPointerException("elementName is null."); }
        if(namespace == null){ throw new NullPointerException("namespace is null."); }
    }
    
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
    	
    	startElement(xml);
        xml.rightAngleBracket();
        childrenElements(xml);
//        closeElement(xml);
        return xml;
    };

    protected void startElement(XmlStringBuilder builder)
    {
//        builder.halfOpenElement(elementName)
//                .xmlnsAttribute(namespace);
    }

    protected  void childrenElements(XmlStringBuilder builder){

    }

    protected void closeElement(XmlStringBuilder builder) {
        builder.closeElement(elementName);
    }

    public String getNamespace() {
        return namespace;
    }

    public String getElementName() {
        return elementName;
    }
}

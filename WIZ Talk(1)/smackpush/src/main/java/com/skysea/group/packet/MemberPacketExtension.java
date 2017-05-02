package com.skysea.group.packet;

import com.skysea.group.GroupService;
import com.skysea.group.MemberInfo;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.xmlpull.v1.XmlPullParser;

/**
 * 成员扩展包。
 * Created by zhangzhi on 2014/9/24.
 */
public class MemberPacketExtension implements PacketExtension {
    public final static String ELEMENT_NAME = "x";
    public final static String NAMESPACE = GroupService.GROUP_MEMBER_NAMESPACE;
    private final MemberInfo memberInfo;

    public MemberPacketExtension(MemberInfo memberInfo) {
        if (memberInfo == null) {
            throw new NullPointerException("memberinfo is null.");
        }
        this.memberInfo = memberInfo;
    }

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public CharSequence toXML() {
        return new XmlStringBuilder()
                .halfOpenElement(ELEMENT_NAME)
                .xmlnsAttribute(NAMESPACE)
                .rightAngelBracket()
                .halfOpenElement("member")
                .optAttribute("username", memberInfo.getUserName())
                .optAttribute("nickname", memberInfo.getNickname())
                .closeEmptyElement()
                .closeElement(ELEMENT_NAME);
    }

    /**
     * 获得成员信息。
     *
     * @return
     */
    public MemberInfo getMemberInfo() {
        return memberInfo;
    }

    public static MemberPacketExtension tryParse(String rootName, String namespace, XmlPullParser pullParser) {
        if (ELEMENT_NAME.equals(rootName) &&
                "member".equals(pullParser.getName()) &&
                NAMESPACE.equals(namespace)) {
            return new MemberPacketExtension(new MemberInfo(
                    pullParser.getAttributeValue(null, "username"),
                    pullParser.getAttributeValue(null, "nickname")));
        }
        return null;
    }
}

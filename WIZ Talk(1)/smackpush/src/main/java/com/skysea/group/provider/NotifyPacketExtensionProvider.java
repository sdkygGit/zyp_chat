package com.skysea.group.provider;

import java.io.IOException;

import com.skysea.group.packet.MemberPacketExtension;
import com.skysea.group.packet.NotifyPacketExtension;
import com.skysea.group.packet.notify.NotifyParser;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * 通知扩展包提供程序。
 * Created by apple on 14-9-24.
 */
public class NotifyPacketExtensionProvider extends ExtensionElementProvider<PacketExtension> {
    private final static String BASE_NAMESPACE = "http://skysea.com/protocol/group";
    
    
	@Override
	public PacketExtension parse(XmlPullParser parser, int initialDepth)
			throws XmlPullParserException, IOException, SmackException {
		// TODO Auto-generated method stub

        String name = parser.getName();
        String namespace = parser.getNamespace();
        PacketExtension extension = null;

        while (true) {
            int type = parser.next();
            if (type == XmlPullParser.START_TAG && extension == null) {

                if (namespace.startsWith(BASE_NAMESPACE) && NotifyParser.isAccept(parser.getName())) {

                    /* 解析通知扩展包 */
                    NotifyPacketExtension packet = new NotifyPacketExtension(name, namespace);
                    try {
						packet.setNotify(NotifyParser.parse(parser, packet.getNamespace()));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    extension = packet;
                } else {
                     /* 解析聊天消息中的程序信息扩展包 */
                    extension = MemberPacketExtension.tryParse(name, namespace, parser);
                }
            } else if (type == XmlPullParser.END_TAG) {
                if (parser.getName().equals(name)) {
                    break;
                }
            }
        }

        return extension;
    }


}

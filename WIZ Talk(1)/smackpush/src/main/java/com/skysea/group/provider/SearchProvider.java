package com.skysea.group.provider;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.ReportedData.Column;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangzhi on 2014/9/17.
 */
public class SearchProvider  extends IQProvider<IQ> {

	@Override
	public IQ parse(XmlPullParser parser, int initialDepth)
			throws XmlPullParserException, IOException, SmackException {
        UserSearch search = null;
        SimpleUserSearch simpleUserSearch = new SimpleUserSearch(null, null);

        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG && parser.getName().equals("instructions")) {
                buildDataForm(simpleUserSearch, parser.nextText(), parser);
                return simpleUserSearch;
            }
            else if (eventType == XmlPullParser.START_TAG && parser.getName().equals("item")) {
                simpleUserSearch.parseItems(parser);
                return simpleUserSearch;
            }
            else if (eventType == XmlPullParser.START_TAG) {
                // Otherwise, it must be a packet extension.
                if(search == null) {
                    search = new UserSearch();
                }


                search.addExtension(PacketParserUtils.parsePacketExtension(parser.getName(),
                        parser.getNamespace(), parser));

            }
            else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals("query")) {
                    done = true;
                }
            }
        }

        if (search != null) {
            return search;
        }
        return simpleUserSearch;
    }

    private static void buildDataForm(SimpleUserSearch search, String instructions, XmlPullParser parser) throws XmlPullParserException, IOException, SmackException  {
        DataForm dataForm = new DataForm(DataForm.Type.form);
        boolean done = false;
        dataForm.setTitle("User Search");
        dataForm.addInstruction(instructions);
        while (!done) {
            int eventType = parser.next();

            if (eventType == XmlPullParser.START_TAG && !parser.getNamespace().equals("jabber:x:data")) {
                String name = parser.getName();
                FormField field = new FormField(name);

                // Handle hard coded values.
                if(name.equals("first")){
                    field.setLabel("First Name");
                }
                else if(name.equals("last")){
                    field.setLabel("Last Name");
                }
                else if(name.equals("email")){
                    field.setLabel("Email Address");
                }
                else if(name.equals("nick")){
                    field.setLabel("Nickname");
                }

                field.setType(FormField.Type.text_single);
                dataForm.addField(field);
            }
            else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals("query")) {
                    done = true;
                }
            }
            else if (eventType == XmlPullParser.START_TAG) {
                search.addExtension(PacketParserUtils.parsePacketExtension(parser.getName(),
                        parser.getNamespace(), parser));
            }
        }
        if (search.getExtension("x", "jabber:x:data") == null) {
            search.addExtension(dataForm);
        }
    }

    /**
     * MySimpleUserSearch is used to support the non-dataform type of XEP 55. This provides
     * the mechanism for allowing always type ReportedData to be returned by any search result,
     * regardless of the form of the data returned from the server.
     *
     * @author Derek DeMoro
     */
    public static class SimpleUserSearch extends IQ {

		protected SimpleUserSearch(String childElementName,
				String childElementNamespace) {
			super(childElementName, childElementNamespace);
			// TODO Auto-generated constructor stub
		}

		private Form form;
        private ReportedData data;

        public void setForm(Form form) {
            this.form = form;
        }

        public ReportedData getReportedData() {
            return data;
        }

        
//		@Override
//		protected IQChildElementXmlStringBuilder getIQChildElementBuilder(
//				IQChildElementXmlStringBuilder xml) {
//			// TODO Auto-generated method stub
//			XmlStringBuilder buf = new XmlStringBuilder();
//            buf.append("<query xmlns=\"jabber:iq:search\">");
//            buf.append(getItemsToSearch());
//            buf.append("</query>");
//            return buf;
//		}


        private String getItemsToSearch() {
            StringBuilder buf = new StringBuilder();

            if (form == null) {
                form = Form.getFormFrom(this);
            }

            if (form == null) {
                return "";
            }

            for (FormField field : form.getFields()) {
                String name = field.getVariable();
                String value = getSingleValue(field);
                if (value.trim().length() > 0) {
                    buf.append("<").append(name).append(">").append(value).append("</").append(name).append(">");
                }
            }

            return buf.toString();
        }

        private static String getSingleValue(FormField formField) {
            List<String> values = formField.getValues();
            if (values.isEmpty()) {
                return "";
            } else {
                return values.get(0);
            }
        }

        protected void parseItems(XmlPullParser parser) throws XmlPullParserException, IOException  {
            ReportedData data = new ReportedData();
            data.addColumn(new Column("JID", "jid", FormField.Type.text_single));

            boolean done = false;

            List<ReportedData.Field> fields = new ArrayList<ReportedData.Field>();
            while (!done) {
                if (parser.getAttributeCount() > 0) {
                    String jid = parser.getAttributeValue("", "jid");
                    List<String> valueList = new ArrayList<String>();
                    valueList.add(jid);
                    ReportedData.Field field = new ReportedData.Field("jid", valueList);
                    fields.add(field);
                }

                int eventType = parser.next();

                if (eventType == XmlPullParser.START_TAG && parser.getName().equals("item")) {
                    fields = new ArrayList<ReportedData.Field>();
                }
                else if (eventType == XmlPullParser.END_TAG && parser.getName().equals("item")) {
                    ReportedData.Row row = new ReportedData.Row(fields);
                    data.addRow(row);
                }
                else if (eventType == XmlPullParser.START_TAG) {
                    String name = parser.getName();
                    String value = parser.nextText();

                    List<String> valueList = new ArrayList<String>();
                    valueList.add(value);
                    ReportedData.Field field = new ReportedData.Field(name, valueList);
                    fields.add(field);

                    boolean exists = false;
                    for (Column column : data.getColumns()) {
                        if (column.getVariable().equals(name)) {
                            exists = true;
                            break;
                        }
                    }

                    // Column name should be the same
                    if (!exists) {
                        Column column = new Column(name, name, FormField.Type.text_single) ;
                        data.addColumn(column);
                    }
                }
                else if (eventType == XmlPullParser.END_TAG) {
                    if (parser.getName().equals("query")) {
                        done = true;
                    }
                }
            }

            this.data = data;
        }

		@Override
		protected IQChildElementXmlStringBuilder getIQChildElementBuilder(
				IQChildElementXmlStringBuilder xml) {
			// TODO Auto-generated method stub
			return null;
		}


    }
}

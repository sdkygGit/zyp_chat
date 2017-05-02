package com.fanning.library;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.ByteArrayEntity;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by ynyxmac on 16/1/4.
 */
public class FNHttp
{
    public final static String Http_Tag = "HttpTag";
    public final static String Http_Value = "HttpValue";
    public final static String Http_Succ = "HttpSucc";


    public static AsyncHttpClient httpClient = new AsyncHttpClient();

    private static class HttpData{
        public android.os.Handler MsgHwnd=null;
        public String MsgTag="";
        public ProgressDialog PD=null;
    }

    public static class ProcessMsgObj implements Serializable {
        public ProgressDialog pdObj=null;
    }

    private static void httpSendMsg(HttpData httpData, String httpReturn,  String successinfo)
    {
        if (httpData == null || httpData.MsgHwnd == null) return;;

        ProcessMsgObj msgobj=null;
        Message msg = new Message();
        Bundle bd = new Bundle();

        if (httpData.PD !=null)
        {
            msgobj = new ProcessMsgObj();
            msgobj.pdObj = httpData.PD;
        }

        //写传递数据
        bd.putString(Http_Tag, httpData.MsgTag);
        bd.putString(Http_Value,  httpReturn);
        bd.putString(Http_Succ, successinfo);

        bd.putSerializable("ProcessObj", msgobj);
        msg.setData(bd);

        //传递消息
        httpData.MsgHwnd.sendMessage(msg);

    }


    private static void syncHttpGet(
            Context AOwner,
            android.os.Handler AMsgHwnd,
            String msgTag,
            String url,
            String msg,
            boolean isShowMessage)
    {
        final HttpData http=new HttpData();

        http.MsgHwnd = AMsgHwnd;
        http.MsgTag  = msgTag;

        if (isShowMessage)
        {
            http.PD = new ProgressDialog(AOwner);
            http.PD.setMessage(msg);
            http.PD.show();
        }

        httpClient.get(url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                if (throwable !=null)
                    s = throwable.toString();
                else if (FNTools.emptyString(s))
                    s = "http get fail";

                FNTools.FLog("http(Get)请求失败[" + http.MsgTag + "]:" + s);
                httpSendMsg(http, s, "fail");
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                FNTools.FLog("http(Get)请求成功[" + http.MsgTag + "]:" + s);
                httpSendMsg(http, s, FNTools.emptyString(s) ? "fail" : "success");
            }
        });
    }


    private static void syncHttpPost(
            Context AOwner,
            android.os.Handler AMsgHwnd,
            String msgTag,
            String url,
            JSONObject jsonObject,
            String msg,
            boolean isShowMessage)
    {
        final HttpData http=new HttpData();

        http.MsgHwnd = AMsgHwnd;
        http.MsgTag  = msgTag;

        if (isShowMessage)
        {
            http.PD = new ProgressDialog(AOwner);
            http.PD.setMessage(msg);
            http.PD.show();
        }

        ByteArrayEntity entity = null;

        try
        {
            entity = new ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
            Log.e("EEEEEEEEEEEE",entity.toString());
        }
        catch (UnsupportedEncodingException e)
        {
            FNTools.FLog("http(Post)请求转码失败[" + http.MsgTag + "]:" + e.getMessage());
            httpSendMsg(http, "http post fail", "fail");
            return;
        }

        httpClient.post(AOwner, url, entity, "application/json", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                if (throwable !=null)
                    s = throwable.toString();
                else if (FNTools.emptyString(s))
                    s = "http post fail";

                FNTools.FLog("http(Post)请求失败[" + http.MsgTag + "]:" + s);
                httpSendMsg(http, s, "fail");
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                FNTools.FLog("http(Post)请求成功[" + http.MsgTag + "]:" + s);
                httpSendMsg(http, s, FNTools.emptyString(s) ? "fail" : "success");
            }
        });
    }


    private static void syncHttpPost(
            Context AOwner,
            android.os.Handler AMsgHwnd,
            String msgTag,
            String url,
            Map<String ,String > map,
            String msg,
            boolean isShowMessage)
    {
        final HttpData http=new HttpData();

        http.MsgHwnd = AMsgHwnd;
        http.MsgTag  = msgTag;

        if (isShowMessage)
        {
            http.PD = new ProgressDialog(AOwner);
            http.PD.setMessage(msg);
            http.PD.show();
        }

        RequestParams params = new RequestParams(map);

        httpClient.post(url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                if (throwable !=null)
                    s = throwable.toString();
                else if (FNTools.emptyString(s))
                    s = "http post fail";

                FNTools.FLog("http(Post)请求失败[" + http.MsgTag + "]:" + s);
                httpSendMsg(http, s, "fail");
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                FNTools.FLog("http(Post)请求成功[" + http.MsgTag + "]:" + s);
                httpSendMsg(http, s, FNTools.emptyString(s) ? "fail" : "success");
            }
        });
    }

    public static void syncHttpPostWithProgress(
            Context AOwner,
            android.os.Handler AMsgHwnd,
            String msgTag,
            String url,
            Map<String ,String > map,
            String msg)
    {
        syncHttpPost(AOwner, AMsgHwnd, msgTag, url, map, msg, true);
    }

    public static void syncHttpPostWithoutProgress(
            Context AOwner,
            android.os.Handler AMsgHwnd,
            String msgTag,
            String url,
            Map<String ,String > map)
    {
        syncHttpPost(AOwner, AMsgHwnd, msgTag, url, map, "", false);
    }

    //
    public static void syncHttpPostWithProgress(
            Context AOwner,
            android.os.Handler AMsgHwnd,
            String msgTag,
            String url,
            JSONObject jsonObject,
            String msg)
    {
        syncHttpPost(AOwner, AMsgHwnd, msgTag, url, jsonObject, msg, false);
    }

    public static void syncHttpPostWithoutProgress(
            Context AOwner,
            android.os.Handler AMsgHwnd,
            String msgTag,
            String url,
            JSONObject jsonObject)
    {
        syncHttpPost(AOwner, AMsgHwnd, msgTag, url, jsonObject, "", false);
    }


    public static void syncHttpGetWithProgress(
            Context AOwner,
            android.os.Handler AMsgHwnd,
            String msgTag,
            String url,
            String msg)
    {
        syncHttpGet(AOwner, AMsgHwnd, msgTag, url, msg, true);
    }

    public static void syncHttpGetWithoutProgress(
            Context AOwner,
            android.os.Handler AMsgHwnd,
            String msgTag,
            String url)
    {
        syncHttpGet(AOwner, AMsgHwnd, msgTag, url, "", false);
    }


    public static String getRequestResult(Bundle bd)
    {
        if (bd == null) return "";
        return bd.getString(Http_Value);
    }

    public static String getRequestTag(Bundle bd)
    {
        if (bd == null) return "";
        return bd.getString(Http_Tag);
    }

    public static boolean isRequestSuccess(Bundle bd)
    {
        if (bd == null || bd.isEmpty()) return false;
        return bd.getString(Http_Succ).toLowerCase().equals("success");
    }

    public static void closeRequestProgress(Bundle bd)
    {
        if (bd == null) return;

        ProcessMsgObj pd = (ProcessMsgObj) bd.getSerializable("ProcessObj");
        if (pd !=null && pd.pdObj != null )
            pd.pdObj.dismiss();
    }
}

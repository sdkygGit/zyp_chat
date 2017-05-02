package com.wiz.dev.wiztalk.view;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.epic.traverse.push.util.L;
import com.lidroid.xutils.util.MimeTypeUtils;
import com.wiz.dev.wiztalk.DB.XmppDbMessage;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.DB.XmppMessageContentProvider;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.utils.SaveConfig;
import com.wiz.dev.wiztalk.utils.SmileyParser;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cn.finalteam.okhttpfinal.FileDownloadCallback;

/**
 * 接收的文本消息
 * Created by Dong on 2015/10/13.
 */
@EViewGroup(R.layout.item_chat_received_docshare)
public class ChatItemReceiveDocShare extends ChatItemView implements View.OnClickListener {

    @ViewById
    TextView tv_message;

    @ViewById
    TextView tv_name;//昵称

    @ViewById
    ImageView img_local;

    @ViewById
    LinearLayout lay_localname;

    JSONObject jsonObject;

    String type;

    @ViewById
    ProgressBar progressBar;

    public ChatItemReceiveDocShare(Context context) {
        super(context);
    }

    @Override
    public void bindOther(XmppMessage message) {
        L.d("ChatItemReceiveTextView message:", message.toString());
        if (XmppDbMessage.isGroupChatMessage(message)) {
//    		 TODO 在群里，extlocalDisplayname用来显示群成员的昵称了
//         	tv_name.setText(message.getExtRemoteDisplayName());
            tv_name.setText(message.getExtLocalDisplayName());
        } else {
            tv_name.setVisibility(View.GONE);
//            XmppDbManager.getInstance(mContext).queryToReadStatusAndSendNotify(message);
        }

        try {
            String msgBody = SmileyParser.getInstance().addSmileySpans(message.getBody()).toString();
            if (msgBody.split("#")[0].equals(SaveConfig.SHARE_PWD)) {

                jsonObject = new JSONObject(msgBody.split("#")[1]);
                if (jsonObject.getString("share_type").equals("svg")) {
                    type = "svg";
                    tv_message.setText(jsonObject.getString("share_title"));
//                    tv_street.setText(jsonObject.getString("share_desc"));
                    img_local.setImageResource(R.mipmap.svg);
                } else if (jsonObject.getString("share_type").equals("pdf")) {
                    type = "pdf";
                    tv_message.setText(jsonObject.getString("share_title"));
//                    tv_street.setText(jsonObject.getString("share_desc"));
                    img_local.setImageResource(R.mipmap.pdf);
                } else if (jsonObject.getString("share_type").equals("pr")) {
                    type = "pr";
                    tv_message.setText(jsonObject.getString("share_title"));
//                    tv_street.setText(jsonObject.getString("share_desc"));
                    img_local.setImageResource(R.mipmap.pr);
                } else if (jsonObject.getString("share_type").equals("dwg")) {
                    type = "dwg";
                    tv_message.setText(jsonObject.getString("share_title"));
//                    tv_street.setText(jsonObject.getString("share_desc"));
                    img_local.setImageResource(R.mipmap.dwg);
                } else if (jsonObject.getString("share_type").equals("excle")) {
                    type = "excle";
                    tv_message.setText(jsonObject.getString("share_title"));
//                    tv_street.setText(jsonObject.getString("share_desc"));
                    img_local.setImageResource(R.mipmap.excle);
                } else if (jsonObject.getString("share_type").equals("doc")) {
                    type = "doc";
                    tv_message.setText(jsonObject.getString("share_title"));
//                    tv_street.setText(jsonObject.getString("share_desc"));
                    img_local.setImageResource(R.mipmap.word);
                }
            }
        } catch (Exception e) {
        }
        lay_localname.setOnClickListener(this);

    }

    @LongClick(R.id.lay_localname)
    public void tv_message() {
        L.d(TAG, "tv_message()");
        initPopupWindow();
        showPopuWindow(lay_localname);
    }

    protected void initPopupWindow() {
        if (popWin == null) {
            popView = LayoutInflater.from(mContext).inflate(
                    R.layout.popup_list_item_long_click, null);
            MyPopuwinOnClickLisentener lisenter = new MyPopuwinOnClickLisentener();


            TextView tvCopy = (TextView) popView.findViewById(R.id.tvCopy);
            tvCopy.setVisibility(View.GONE);
            View line = popView.findViewById(R.id.line);
            line.setVisibility(View.GONE);


            TextView tvDel = (TextView) popView.findViewById(R.id.tvDel);
            tvDel.setOnClickListener(lisenter);

            popWin = new PopupWindow(popView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            popWin.setOutsideTouchable(true);
            popView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    popWin.dismiss();
                    // todo
                }
            });
            // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
            // 我觉得这里是API的一个bug
            popWin.setBackgroundDrawable(new BitmapDrawable());
        }
    }

    private class MyPopuwinOnClickLisentener implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tvCopy:

                    ClipboardManager cm = (ClipboardManager) mContext
                            .getSystemService(Activity.CLIPBOARD_SERVICE);
                    cm.setPrimaryClip(ClipData.newPlainText(message.getBody(), message.getBody()));
                    Toast.makeText(
                            mContext,
                            mContext.getResources().getString(
                                    R.string.popuwin_tips_copy_suc),
                            Toast.LENGTH_SHORT).show();

                    break;
                case R.id.tvDel:
                    messageDao.delete(message);
                    mContext.getContentResolver().notifyChange(
                            XmppMessageContentProvider.CONTENT_URI, null);
                    break;
                default:
                    break;
            }
            popWin.dismiss();
        }
    }

    public void onClick(View view) {
        if (jsonObject != null) {
            try {
                downFile(jsonObject.getString("share_id").replaceAll("\\\\", ""), jsonObject.getString("share_title"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void downFile(final String downloadUrl, final String fileName) {
        final String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wizTalk/" + fileName;
        final File saveFile = new File(filePath);
        if (saveFile.exists())
            openDoc(saveFile);
        else
            cn.finalteam.okhttpfinal.HttpRequest.download(downloadUrl, saveFile, new FileDownloadCallback() {
                @Override
                public void onStart() {
                    super.onStart();
                    progressBar.setVisibility(View.VISIBLE);
                }

                //下载进度
                @Override
                public void onProgress(int progress, long networkSpeed) {
                    super.onProgress(progress, networkSpeed);
                    progressBar.setProgress(progress);
                }

                //下载失败
                @Override
                public void onFailure() {
                    super.onFailure();
                    if (saveFile.exists())
                        saveFile.delete();
                }

                //下载完成（下载成功）
                @Override
                public void onDone() {
                    openDoc(saveFile);
                }
            });
    }

    private void openDoc(File saveFile) {
        if (type.equals("svg")) {
            Intent intent = new Intent();
            intent.putExtra("filePath", saveFile.getAbsolutePath());
//            intent.setClass(mContext, H5Activity_.class);
            intent.setAction("com.yxst.epic.unifyplatform..activity.SvgActivity");
            mContext.startActivity(intent);
        } else if (type.equals("pdf")) {
            Intent intent=new Intent();
            intent.setAction("android.intent.action.OpenPulgReservicer");
//                intent.setData(uri);
            intent.putExtra("command","openPdf");
            intent.putExtra("filePath",saveFile.getAbsolutePath());
            mContext.sendBroadcast(intent);
        } else if (type.equals("dwg")) {
            openFile(saveFile);
        } else if (type.equals("dwg")) {
            openFile(saveFile);
        } else if (type.equals("pr")) {
            Toast.makeText(mContext, "pr", Toast.LENGTH_SHORT).show();
//                    openFile(saveFile);
        } else if (type.equals("excle")) {
            openFile(saveFile);
        } else if (type.equals("ppt")) {
            openFile(saveFile);
        } else if (type.equals("doc")) {
            openFile(saveFile);
        }
    }

    private void openFile(File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);//获取文件file的MIME类型
        String type = MimeTypeUtils.getMimeType(file.getName());//设置intent的data和Type属性。
        intent.setDataAndType(Uri.fromFile(file), type);
        try {
            mContext.startActivity(intent); //这里最好try一下，有可能会报错。 //比如说你的MIME类型是打开邮箱，但是你手机里面没装邮箱客户端，就会报错。
        } catch (Exception e) {
            Toast.makeText(mContext, "没有安装相应的软件，请安装后打开", Toast.LENGTH_SHORT).show();
        }
    }
}

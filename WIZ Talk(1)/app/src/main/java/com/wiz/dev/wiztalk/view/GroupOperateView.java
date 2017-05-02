package com.wiz.dev.wiztalk.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.epic.traverse.push.smack.XmppManager;
import com.epic.traverse.push.util.L;
import com.skysea.group.Group;
import com.skysea.group.GroupService;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.DB.XmppDbManager;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.DB.XmppMessageContentProvider;
import com.wiz.dev.wiztalk.DB.XmppMessageDao;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.public_store.OpenfireConstDefine;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;

@EViewGroup(R.layout.item_group_operate)
public class GroupOperateView extends RelativeLayout {

	private static final String TAG = "GroupOperateView";

	@ViewById
	ImageView iv_group_avatar;

	@ViewById
	TextView tv_group_name;

	@ViewById
	TextView tv_group_msg;
	@ViewById
	TextView tv_group_operate_user;

	@ViewById
	Button btn_group_operate_status;
	
	@ViewById
	TextView tv_group_operate_status;

	@ViewById
	View tip_lay;

	private Context mContext;
	private XmppMessage message;
	protected XmppMessageDao messageDao;

	public GroupOperateView(Context context) {
		super(context);
		this.mContext = context;
		messageDao = MyApplication.getDaoSession(context).getXmppMessageDao();
	}

	public void bind(XmppMessage msg) {
		L.d(TAG, msg.toString());
		message = msg;
		if(!TextUtils.isEmpty(message.getExtLocalDisplayName()))
			tv_group_name.setText(message.getExtLocalDisplayName());
		if(!TextUtils.isEmpty(message.getBody()))
			tv_group_msg.setText(message.getBody());
		if(message.getExtGroupOperateType() ==  MsgInFo.OPERATE_TIPS){
			tv_group_operate_user.setText("处理人: "+message.getExtGroupOperateUserNick());
			btn_group_operate_status.setVisibility(View.GONE);
		}else if ( message.getExtGroupOperateType() ==  MsgInFo.OPERATE_INVITE){
			tv_group_operate_user.setText("邀请人: "+message.getExtGroupOperateUserNick());
			if(message.getExtGroupOperateIsdeal() == null){
				btn_group_operate_status.setVisibility(View.VISIBLE);
				tv_group_operate_status.setVisibility(View.GONE);
			}else if(message.getExtGroupOperateIsdeal()==MsgInFo.OPERATE_INVITE_STATUS){
				tv_group_operate_status.setVisibility(View.VISIBLE);
				btn_group_operate_status.setVisibility(View.GONE);
			}
		}else if (message.getExtGroupOperateType() == MsgInFo.OPERATE_APPLY){
			tv_group_operate_user.setText("申请人: "+message.getExtGroupOperateUserNick());
			if(message.getExtGroupOperateIsdeal() == null){
				btn_group_operate_status.setVisibility(View.VISIBLE);
			}else if(message.getExtGroupOperateIsdeal()==MsgInFo.OPERATE_APPLY_STATUS){
				tv_group_operate_status.setVisibility(View.VISIBLE);
				btn_group_operate_status.setVisibility(View.GONE);
			}
		}
	}



	@Click
	void btn_group_operate_status(View v){
		L.d(TAG, message.toString());

		if (message.getExtGroupOperateType() == MsgInFo.OPERATE_INVITE) {
			//同意邀请
			doInbackground();
		} else if (message.getExtGroupOperateType() == MsgInFo.OPERATE_APPLY) {
			// 同意申请
			doInbackgroundDealApply();
		}

	}
	//        XmppMessage{id=103, sid='null', to_='19@group.skysea.com', 
// from_='00059f7f10594bec8a4d29abd1e29e52@skysea.com', 
// type='group_operate', body='邹明申请加入群 ', direct=1, createTime=1458034567500, mold=0, 
// voiceLength=null, filePath='null', status=0, readStatus=1, 
// extLocalUserName='00059f7f10594bec8a4d29abd1e29e52@skysea.com',
// extRemoteUserName='group.skysea.com', extLocalDisplayName='test3',
// extRemoteDisplayName='群通知', extGroupOperateType=3, extGroupOperateIsdeal=4, 
// extGroupOperateUserName='42039767747a40b6b98a7a11c0c26a12', 
// extGroupOperateUserNick='邹明', extGroupMemberUserName='null', extGroupMemberUserNick='null'}
	@Background
	void doInbackgroundDealApply(){
		onPreExecute();
		try {
			GroupService  groupService = XmppManager.getInstance().getGroupService(OpenfireConstDefine.OPENFIRE_SERVER_NAME);
			String groupJid = message.getTo_();

			Group group = groupService.getGroup(groupJid);
			String username =message.getExtGroupOperateUserName();//
			String nickname = message.getExtGroupOperateUserNick();

			if (username.contains("@")) {
				username = username.substring(0, username.lastIndexOf("@"));
			}
			
			group.processApply(groupJid, username, nickname, true, "欢迎加入群");
            message.setReadStatus(MsgInFo.READ_TRUE);
			message.setShowTime(System.currentTimeMillis());
            XmppDbManager.getInstance(getContext()).updateWithNotify(message);
			onPostExecute(true,OPERATE_2);
		} catch (Exception e) {
			e.printStackTrace();
			onPostExecute(false,OPERATE_2);
		} 

	}
	
	
	private ProgressDialog mProgressDialog;

	@UiThread
	void onPreExecute() {
		mProgressDialog = ProgressDialog.show(mContext, "提示", "正在操作...");
	}
//	XmppMessage{id=156, sid='null', to_='15aadcdfb8a24f91895c3190c8e82c99@skysea.com', 
// from_='5@group.skysea.com', type='group_operate', body='邀请你加群',
// direct=1, createTime=1458121854671, mold=0, voiceLength=null, 
// filePath='null', status=0, readStatus=1, 
// extLocalUserName='15aadcdfb8a24f91895c3190c8e82c99@skysea.com',
// extRemoteUserName='group.skysea.com', 
// extLocalDisplayName='test3', extRemoteDisplayName='群通知', extGroupOperateType=1, extGroupOperateIsdeal=2, extGroupOperateUserName='8aef70993faea3a4013fc622267d052f', extGroupOperateUserNick='高铮勇', extGroupMemberUserName='null', extGroupMemberUserNick='null'}
	@Background
	void doInbackground(){
		onPreExecute();
		try {
			GroupService  groupService = XmppManager.getInstance().getGroupService(OpenfireConstDefine.OPENFIRE_SERVER_NAME);
			String groupJid = message.getTo_();
			
			Group group = groupService.getGroup(groupJid);
			String nikname =MyApplication.getInstance().getLocalMember().NickName;
			String username = message.getExtLocalUserName();
			if (username.contains("@")) {
				username = username.substring(0, username.lastIndexOf("@"));
			}
			group.processInvite(username, nikname, true, "很高兴加入群！");
            message.setReadStatus(MsgInFo.READ_TRUE);
			message.setShowTime(System.currentTimeMillis());
            XmppDbManager.getInstance(getContext()).updateWithNotify(message);

            onPostExecute(true,OPERATE_1);
		} catch (NotConnectedException e) {
			e.printStackTrace();
			onPostExecute(false,OPERATE_1);
		} catch (XMPPException.XMPPErrorException e) {
			e.printStackTrace();
		} catch (SmackException.NoResponseException e) {
			e.printStackTrace();
		}
	}

	private final int OPERATE_1 = 1;//处理邀请，
	private final int OPERATE_2 = 2;//处理申请

	/**
	 * 
	 * @param result
	 * @param operateType
     */
	@UiThread
	void onPostExecute(boolean result,int operateType) {
		Log.d(TAG, "onPostExecute() response:" + result);
		dismissProgressDialog();
		if (result) {
			Toast.makeText(mContext, "操作成功",
					Toast.LENGTH_SHORT).show();
			if (operateType == OPERATE_1) {
				message.setExtGroupOperateIsdeal(MsgInFo.OPERATE_INVITE_STATUS);
			}
			if (operateType == OPERATE_2) {
				message.setExtGroupOperateIsdeal(MsgInFo.OPERATE_APPLY_STATUS);
			}
			XmppDbManager.getInstance(mContext).updateWithNotify(message);
		}else{
			Toast.makeText(mContext, "操作失败",
					Toast.LENGTH_SHORT).show();
		}
		// changeData(response.memberList);
	}
	private void dismissProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	@LongClick(R.id.tip_lay)
	public void layoutVoice() {
		Log.d(TAG, "layout_voice()");
		initPopupWindow();
		showPopuWindow(tip_lay);
	}

	PopupWindow popWin;
	protected View popView;
	protected void initPopupWindow() {
		if (popWin == null) {
			popView = LayoutInflater.from(mContext).inflate(
					R.layout.popup_list_item_long_click, null);

			View copyLay = (View) popView.findViewById(R.id.tvCopy);
			copyLay.setVisibility(View.GONE);
			View line = (View) popView.findViewById(R.id.line);
			line.setVisibility(View.GONE);

			TextView tvDel = (TextView) popView.findViewById(R.id.tvDel);
			tvDel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					messageDao.delete(message);
					mContext.getContentResolver().notifyChange(
							XmppMessageContentProvider.CONTENT_URI, null);
					popWin.dismiss();
				}
			});

			popWin = new PopupWindow(popView,
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			popWin.setOutsideTouchable(true);
			popView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					popWin.dismiss();
				}
			});
			// 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
			// 我觉得这里是API的一个bug
			popWin.setBackgroundDrawable(new BitmapDrawable());
		}
	}

	protected void showPopuWindow(View iv) {
		popView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		int popupWidth = popView.getMeasuredWidth();
		int popupHeight = popView.getMeasuredHeight();
		int[] location = new int[2];
		iv.getLocationOnScreen(location);
		popWin.showAtLocation(iv, Gravity.NO_GRAVITY,
				(location[0] + iv.getWidth() / 2) - popupWidth / 2, location[1]
						- popupHeight);
	}
}

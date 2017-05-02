package com.wiz.dev.wiztalk.view.dialog;


import android.app.Activity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.utils.SaveConfig;

/**
 * Created by hekang on 2016/3/29.
 */
public class ConfigAddressPopuWindow {
	private Activity context;
	private PopupWindow popupWindow;
	SaveConfig saveConfig;

	public ConfigAddressPopuWindow(Activity context) {
		this.context = context;
		saveConfig = new SaveConfig(context);
		View view = LayoutInflater.from(context).inflate(
				R.layout.popwindow_set, null);
		initSet(view);
		initView(view);
	}

	private void initView(View view) {
		final EditText editText_login = (EditText) view
				.findViewById(R.id.popWindow_edit_login);
		editText_login.setText(saveConfig
				.getStringConfig("login_servier_address"));
		final EditText editText_port = (EditText) view
				.findViewById(R.id.popWindow_edit_login_port);
		editText_port.setText(saveConfig.getStringConfig("login_servier_port"));
		final EditText editText_config = (EditText) view
				.findViewById(R.id.popWindow_edit_config);
		editText_config.setText(saveConfig
				.getStringConfig("config_servier_address"));
		final EditText editText_port2 = (EditText) view
				.findViewById(R.id.popWindow_edit_config_port);
		editText_port2.setText(saveConfig
				.getStringConfig("config_servier_port"));
		Button button_ok = (Button) view.findViewById(R.id.popWindow_button_ok);
		button_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SaveConfig saveConfig = new SaveConfig(context);
				String login = editText_login.getText().toString() + "";
				String port = editText_port.getText().toString() + "";
				String port2 = editText_port2.getText().toString() + "";
				String config = editText_config.getText().toString() + "";

				if (!TextUtils.isEmpty(login) && !TextUtils.isEmpty(port)
						&& !TextUtils.isEmpty(port2)
						&& !TextUtils.isEmpty(config)) {
					
					if (login.matches(SaveConfig.IP_Z) &&config.matches(SaveConfig.IP_Z) ){
						if(port.matches(SaveConfig.PORT_Z) && port2.matches(SaveConfig.PORT_Z)){
							saveConfig.setStringConfig("login_servier_address", login);
							saveConfig.setStringConfig("login_servier_port", port);
							saveConfig.setStringConfig("config_servier_port", port2);
							saveConfig
							.setStringConfig("config_servier_address", config);
							Toast.makeText(context, "配置保存成功", Toast.LENGTH_SHORT)
							.show();
							popupWindow.dismiss();
							
						}else{
							Toast.makeText(context, "端口号格式不正确,保存失败", Toast.LENGTH_SHORT)
							.show();
						}
					}else{
						Toast.makeText(context, "ip地址格式不正确,保存失败", Toast.LENGTH_SHORT)
						.show();
					}
					
				} else {
					Toast.makeText(context, "配置信息不能为空！", Toast.LENGTH_SHORT)
					.show();
				}
			}
		});
		Button button_cancle = (Button) view
				.findViewById(R.id.popWindow_button_cancle);
		button_cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
			}
		});
	}

	private void initSet(View view) {
		popupWindow = new PopupWindow(view);// 把layout设置在popwindow
		// 设置popWindow的宽高
		popupWindow.setWidth((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 320, context.getResources().getDisplayMetrics()));
		popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
		// popupWindow.setWindowLayoutMode(LayoutParams.WRAP_CONTENT,
		// LayoutParams.WRAP_CONTENT);
		popupWindow.setFocusable(true);// 设置可点
		popupWindow.update();// 状态刷新
		popupWindow.setOutsideTouchable(true);// 设置点击外部隐藏popwindow,这个方法设置的前提是popWindow有背景
		popupWindow.setBackgroundDrawable(context.getResources().getDrawable(
				R.drawable.popwindow_bg));// 这个是popupWindow的
		popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);// 这里的动画特效可以自定义
	}

	public void showPopupWindow(View parent) {// 显示popupWindow
		if (!popupWindow.isShowing()) {
			popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);// 设置显示的位置
		} else {
			popupWindow.dismiss();
		}
	}
}

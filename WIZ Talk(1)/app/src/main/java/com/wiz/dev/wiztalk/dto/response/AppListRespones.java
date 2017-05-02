package com.wiz.dev.wiztalk.dto.response;

import com.wiz.dev.wiztalk.dto.model.AppInfo;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class AppListRespones implements Serializable{
	
	private boolean succeed;
	private int code;
	private String msg;
	private List<AppInfo> data;
	
	public boolean isSucceed() {
		return succeed;
	}
	public void setSucceed(boolean succeed) {
		this.succeed = succeed;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public List<AppInfo> getData() {
		return data;
	}
	public void setData(List<AppInfo> data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "AppListRespones [succeed=" + succeed + ", code=" + code
				+ ", msg=" + msg + ", data=" + data + "]";
	}
}

package com.wiz.dev.wiztalk.dto.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class AppInfo implements Serializable{
	
	private int id;
	private String code;
	
	private String name;
	
	private String todos;

	private String openType;
	private String openUrl;
	private int sort;


	public AppInfo(String name ,String todos,String openUrl){
		this.name = name;
		this.todos = todos;
		this.openUrl = openUrl;
	}
	private String avatar;
	
	public AppInfo() {
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTodos() {
		return todos;
	}
	public void setTodos(String todos) {
		this.todos = todos;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public String getOpenType() {
		return openType;
	}
	public void setOpenType(String openType) {
		this.openType = openType;
	}
	public String getOpenUrl() {
		return openUrl;
	}
	public void setOpenUrl(String openUrl) {
		this.openUrl = openUrl;
	}
	@Override
	public String toString() {
		return "AppInfo [id=" + id + ", code=" + code + ", name=" + name
				+ ", todos=" + todos + ", openType=" + openType + ", openUrl="
				+ openUrl + ", sort=" + sort + ", avatar=" + avatar + "]";
	}
}

package com.wiz.dev.wiztalk.model;
/**
 * 显示的title
 * @author Admin
 *
 */
public class AppTitle {
	private String id; //应用id
	private String title;

	public AppTitle(String id, String title) {
		super();
		this.id = id;
		this.title = title;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}

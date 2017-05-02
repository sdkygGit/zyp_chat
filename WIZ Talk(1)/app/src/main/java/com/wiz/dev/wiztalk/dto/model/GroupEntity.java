package com.wiz.dev.wiztalk.dto.model;

import java.io.Serializable;

/**
 * openfire 返回的group对象
 * @author Admin
 *
 */
public class GroupEntity implements Serializable{
	private String jid;
	private String name;
	private String description;
	public String getJid() {
		return jid;
	}
	public void setJid(String jid) {
		this.jid = jid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}

package com.wiz.dev.wiztalk.dto.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Tenant implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6367394014557272044L;

	/**
	 *  
	 *"name":"和合科技有限公司",
      "uuid":"47eeeb5fe1ff4759b2ec82756488a0fa",
      "ruserId":"23806133203991344",
      "id":"23806133203991345",
      "icon":"10,05f520295660",
      "namespace":"h5555f",
      "status":"0",
      "type":"0"
	 */
	
	public String  name;		// 公司名
	public String  uuid;		// 唯一用户标示
	public String  ruserId;		// 认不得
	public String  id;			// 公司ID
	public String  icon;		// 公司图标
	public String  namespace;	// 公司标示 用于应用消息
	public int status;		// 认不得
	public int type;		// 认不得
	
}

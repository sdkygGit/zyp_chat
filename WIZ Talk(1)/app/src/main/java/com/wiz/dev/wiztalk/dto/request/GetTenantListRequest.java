package com.wiz.dev.wiztalk.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetTenantListRequest extends Request{

	/**
	 * 用户名
	 */
	@JsonProperty(value="userName")
	public String UserName;

	/**
	 * 密码
	 */
	@JsonProperty(value="password")
	public String Password;
	
}

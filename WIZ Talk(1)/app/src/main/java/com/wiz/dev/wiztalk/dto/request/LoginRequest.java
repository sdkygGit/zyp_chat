package com.wiz.dev.wiztalk.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequest extends Request {

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

	/**
	 * 公司代号
	 */
	@JsonProperty(value="tenantId")
	public String TenantId;
}

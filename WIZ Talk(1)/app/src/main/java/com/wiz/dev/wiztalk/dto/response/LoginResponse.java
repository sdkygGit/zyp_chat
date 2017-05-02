package com.wiz.dev.wiztalk.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponse extends Response {

	private static final long serialVersionUID = -1333609560593757271L;

	/**
	 * 登录token
	 */
	@JsonProperty(value = "token")
	public String Token;

	@JsonProperty(value = "uid")
	public String Uid;
	
	@JsonProperty(value = "member")
	public com.wiz.dev.wiztalk.DB.Member Member;
	
	@JsonProperty(value = "paasBaseUrl")
	public String PaasBaseUrl;
    
    

}

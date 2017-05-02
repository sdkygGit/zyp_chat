package com.wiz.dev.wiztalk.dto.request;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * 用户关注的模块请求
 * @author Admin
 *
 */
public class AppListRequest implements Serializable{
	
	
	@JsonProperty(value = "uid")
	public String uid;
	@JsonProperty(value = "token")
	public String token;
}

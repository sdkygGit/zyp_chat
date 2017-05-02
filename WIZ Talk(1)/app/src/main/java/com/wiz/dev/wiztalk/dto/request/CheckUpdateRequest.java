package com.wiz.dev.wiztalk.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CheckUpdateRequest extends Request {

	@JsonProperty(value="versionCode")
	public int VersionCode;
	
	@JsonProperty(value="versionName")
	public String VersionName;
	
	@JsonProperty(value="type")
	public String Type = "Android";
	
}

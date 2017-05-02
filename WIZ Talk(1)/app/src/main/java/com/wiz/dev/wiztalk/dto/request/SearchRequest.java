package com.wiz.dev.wiztalk.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchRequest extends Request {

	public static final String SEARCH_TYPE_APP = "app";
	public static final String SEARCH_TYPE_USER = "user";
	
	@JsonProperty(value="searchKey")
	public String SearchKey;
	
	@JsonProperty(value="searchType")
	public String SearchType;
	
	@JsonProperty(value="pageCount")
	public int pageCount;
	
	@JsonProperty(value="pageNo")
	public int pageNo;


	//public int pageNo;

	//public int pageCount;
	
}

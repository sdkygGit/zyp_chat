package com.wiz.dev.wiztalk.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wiz.dev.wiztalk.dto.model.Tenant;
import com.wiz.dev.wiztalk.dto.response.Response;

import java.util.List;

public class GetTenantListResponse extends Response {

	/**
	 * 自动生成的
	 */
	private static final long serialVersionUID = 5869109206490972778L;

	@JsonProperty(value = "tenantList")
	public List<Tenant> TenantList;
}

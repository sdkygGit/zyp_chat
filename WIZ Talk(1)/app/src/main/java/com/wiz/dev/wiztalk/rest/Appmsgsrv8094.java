package com.wiz.dev.wiztalk.rest;

import com.wiz.dev.wiztalk.dto.request.AddOrRemoveContactRequest;
import com.wiz.dev.wiztalk.dto.request.ChangeUserInfoRequest;
import com.wiz.dev.wiztalk.dto.request.CheckPlugUpdateRequest;
import com.wiz.dev.wiztalk.dto.request.CheckUpdateRequest;
import com.wiz.dev.wiztalk.dto.request.FollowAppRequest;
import com.wiz.dev.wiztalk.dto.request.GetAppOperationListRequest;
import com.wiz.dev.wiztalk.dto.request.GetApplicationListRequest;
import com.wiz.dev.wiztalk.dto.request.GetMemberRequest;
import com.wiz.dev.wiztalk.dto.request.GetMemberResponse;
import com.wiz.dev.wiztalk.dto.request.GetOrgInfoRequest;
import com.wiz.dev.wiztalk.dto.request.GetOrgUserListRequest;
import com.wiz.dev.wiztalk.dto.request.GetTenantListRequest;
import com.wiz.dev.wiztalk.dto.request.GetTenantListResponse;
import com.wiz.dev.wiztalk.dto.request.LoginRequest;
import com.wiz.dev.wiztalk.dto.request.SearchRequest;
import com.wiz.dev.wiztalk.dto.request.SetUserAvatarRequest;
import com.wiz.dev.wiztalk.dto.request.SetUserInfoRequest;
import com.wiz.dev.wiztalk.dto.request.UnFollowAppRequest;
import com.wiz.dev.wiztalk.dto.response.AddOrRemoveContactResponse;
import com.wiz.dev.wiztalk.dto.response.ChangeUserInfoResponse;
import com.wiz.dev.wiztalk.dto.response.CheckPlugUpdateResponse;
import com.wiz.dev.wiztalk.dto.response.CheckUpdateResponse;
import com.wiz.dev.wiztalk.dto.response.GetAppOperationListResponse;
import com.wiz.dev.wiztalk.dto.response.GetApplicationListResponse;
import com.wiz.dev.wiztalk.dto.response.GetOrgInfoResponse;
import com.wiz.dev.wiztalk.dto.response.GetOrgUserListResponse;
import com.wiz.dev.wiztalk.dto.response.LoginResponse;
import com.wiz.dev.wiztalk.dto.response.Response;
import com.wiz.dev.wiztalk.dto.response.SearchResponse;
import com.wiz.dev.wiztalk.dto.response.SetUserAvatarResponse;
import com.wiz.dev.wiztalk.dto.response.SetUserInfoResponse;
import com.wiz.dev.wiztalk.public_store.ConstDefine;

import org.androidannotations.annotations.rest.Accept;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.api.rest.MediaType;
import org.androidannotations.api.rest.RestClientErrorHandling;
import org.androidannotations.api.rest.RestClientRootUrl;
import org.androidannotations.api.rest.RestClientSupport;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

//@Rest(rootUrl = "http://115.29.107.77:8094/app/client/device", converters = { MappingJackson2HttpMessageConverter.class }, interceptors = { HttpBasicAuthenticatorInterceptor.class })
// @Rest(rootUrl = "http://115.29.226.14:8093/app/client/device", converters = {
// MappingJackson2HttpMessageConverter.class }, interceptors = {
// HttpBasicAuthenticatorInterceptor.class })
//@Rest(rootUrl = "http://112.74.104.234:80/app/client/device", converters = { MappingJackson2HttpMessageConverter.class, StringHttpMessageConverter.class }, interceptors = { HttpBasicAuthenticatorInterceptor.class })
@Rest(rootUrl = ConstDefine.HttpAdress + "/app/client/device", converters = { MappingJackson2HttpMessageConverter.class, StringHttpMessageConverter.class }, interceptors = { HttpBasicAuthenticatorInterceptor.class })
//@Rest(rootUrl = "http://10.180.120.157:8094/app/client/device", converters = { MappingJackson2HttpMessageConverter.class, StringHttpMessageConverter.class }, interceptors = { HttpBasicAuthenticatorInterceptor.class })
@Accept(MediaType.APPLICATION_JSON)
public interface Appmsgsrv8094 extends RestClientErrorHandling,
		RestClientRootUrl, RestClientSupport {

//	void setHeader(String name, String value);
//
//	String getHeader(String name);
	
	/**
	 * 8094
	 * @param request
	 * @return
	 */
	@Post("/login")
//	@RequiresHeader(value = {"Connection"})
	LoginResponse login(LoginRequest request);
	
	
	/**
	 * 8094
	 * @param request
	 * @return
	 */
	@Post("/getTenantList")
//	@RequiresHeader(value = {"Connection"})
	GetTenantListResponse getTenantList(GetTenantListRequest request);

	/**
	 * 8094
	 * @param request
	 * @return
	 */
	@Post("/addOrRemoveContact")
	AddOrRemoveContactResponse addOrRemoveContact(
			AddOrRemoveContactRequest request);
	/**
	 * 8094
	 * @param request
	 * @return
	 */
	@Post("/getOrgInfo")
	GetOrgInfoResponse getOrgInfo(GetOrgInfoRequest request);
	/**
	 * 8094
	 * @param request
	 * @return
	 */
	@Post("/getMember")
	GetMemberResponse getMember(GetMemberRequest request);
	/**
	 * 8094
	 * @param request
	 * @return
	 */
	@Post("/getOrgUserList")
	GetOrgUserListResponse getOrgUserList(GetOrgUserListRequest request);

	

	

	/**
	 * 8094
	 * @param request
	 * @return
	 */
	@Post("/search")
	SearchResponse search(SearchRequest request);

	/**
	 * 8094
	 * @param request
	 * @return
	 */
	@Post("/checkUpdate")
	CheckUpdateResponse checkUpdate(CheckUpdateRequest request);


	/**
	 * 8094
	 * @param request
	 * @return
	 */
	@Post("/checkPlugUpdate")
	CheckPlugUpdateResponse checkPlugUpdate(CheckPlugUpdateRequest request);



	/**
	 * 8094
	 * @param request
	 * @return
	 */
	@Post("/getApplicationList")
	GetApplicationListResponse getApplicationList(
			GetApplicationListRequest request);



	/**
	 * 8094
	 * @param request
	 * @return
	 */
	@Post("/getAllApplicationList")
	GetApplicationListResponse getAllApplicationList(
			GetApplicationListRequest request);

	/**
	 * 8094
	 * @param request
	 * @return
	 */
	@Post("/setUserAvatar")
	SetUserAvatarResponse setUserAvatar(SetUserAvatarRequest request);
	
	/**
	 * 8094
	 * @param request
	 * @return
	 */
	@Post("/setUserInfo")
	SetUserInfoResponse setUserInfo(SetUserInfoRequest request);
	
	
	/**
	 * 8094
	 * @param request
	 * @return
	 */
	@Post("/followApp")
	Response followApp(FollowAppRequest request);
	
	/**
	 * 8094
	 * @param request
	 * @return
	 */
	@Post("/unFollowApp")
	Response unFollowApp(UnFollowAppRequest request);


	/**
	 * 8094
	 * @param request
	 * @return
	 */
	@Post("/getAppOperationList")
	GetAppOperationListResponse getAppOperationList(
			GetAppOperationListRequest request);

	/**
	 * 8094
	 * @param request
	 * @return
	 */
	@Post("/changeUserInfo")
	ChangeUserInfoResponse changeUserInfo(
			ChangeUserInfoRequest request);
}

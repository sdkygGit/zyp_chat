package com.wiz.dev.wiztalk.rest;

import com.wiz.dev.wiztalk.dto.request.AddOrRemoveContactRequest;
import com.wiz.dev.wiztalk.dto.request.CheckUpdateRequest;
import com.wiz.dev.wiztalk.dto.request.GetApplicationListRequest;
import com.wiz.dev.wiztalk.dto.request.GetMemberRequest;
import com.wiz.dev.wiztalk.dto.request.GetMemberResponse;
import com.wiz.dev.wiztalk.dto.request.GetOrgInfoRequest;
import com.wiz.dev.wiztalk.dto.request.GetOrgUserListRequest;
import com.wiz.dev.wiztalk.dto.request.LoginRequest;
import com.wiz.dev.wiztalk.dto.request.SearchRequest;
import com.wiz.dev.wiztalk.dto.request.SetUserAvatarRequest;
import com.wiz.dev.wiztalk.dto.request.SetUserInfoRequest;
import com.wiz.dev.wiztalk.dto.response.AddOrRemoveContactResponse;
import com.wiz.dev.wiztalk.dto.response.CheckUpdateResponse;
import com.wiz.dev.wiztalk.dto.response.GetApplicationListResponse;
import com.wiz.dev.wiztalk.dto.response.GetOrgInfoResponse;
import com.wiz.dev.wiztalk.dto.response.GetOrgUserListResponse;
import com.wiz.dev.wiztalk.dto.response.LoginResponse;
import com.wiz.dev.wiztalk.dto.response.SearchResponse;
import com.wiz.dev.wiztalk.dto.response.SetUserAvatarResponse;
import com.wiz.dev.wiztalk.dto.response.SetUserInfoResponse;

import org.androidannotations.annotations.rest.Accept;
import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.api.rest.MediaType;
import org.androidannotations.api.rest.RestClientErrorHandling;
import org.androidannotations.api.rest.RestClientRootUrl;
import org.androidannotations.api.rest.RestClientSupport;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Rest(rootUrl = "http://115.29.107.77:8093/app/client/device", converters = { MappingJackson2HttpMessageConverter.class }, interceptors = { HttpBasicAuthenticatorInterceptor.class })
//@Rest(rootUrl = "https://10.180.128.127/app/client/device", converters = { MappingJackson2HttpMessageConverter.class, StringHttpMessageConverter.class }, interceptors = { HttpBasicAuthenticatorInterceptor.class })
// @Rest(rootUrl = "http://115.29.226.14:8093/app/client/device", converters = {
// MappingJackson2HttpMessageConverter.class }, interceptors = {
// HttpBasicAuthenticatorInterceptor.class })
//@Rest(rootUrl = "http://192.168.1.122:8093/app/client/device", converters = { MappingJackson2HttpMessageConverter.class }, interceptors = { HttpBasicAuthenticatorInterceptor.class })
@Accept(MediaType.APPLICATION_JSON)
public interface Appmsgsrv8094Https extends RestClientErrorHandling,
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
	@Post("/getOrgInfo")
	GetOrgInfoResponse getOrgInfo(GetOrgInfoRequest request);

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
	@Post("/addOrRemoveContact")
	AddOrRemoveContactResponse addOrRemoveContact(
			AddOrRemoveContactRequest request);

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
	@Post("/getApplicationList")
	GetApplicationListResponse getApplicationList(
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
	
	@Accept(MediaType.TEXT_PLAIN)
	@Get("/getWelcomeImg")
	String getWelcomeImg();
}

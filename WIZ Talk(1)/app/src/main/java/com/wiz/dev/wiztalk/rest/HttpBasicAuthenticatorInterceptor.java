package com.wiz.dev.wiztalk.rest;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class HttpBasicAuthenticatorInterceptor implements
		ClientHttpRequestInterceptor {
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] data,
			ClientHttpRequestExecution execution) throws IOException {
		// do something
		return execution.execute(request, data);
	}
}

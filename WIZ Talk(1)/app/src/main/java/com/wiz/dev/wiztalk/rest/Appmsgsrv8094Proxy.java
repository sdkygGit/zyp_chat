package com.wiz.dev.wiztalk.rest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import android.content.Context;
import android.util.Log;

import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.dto.response.BaseResponse;
import com.wiz.dev.wiztalk.dto.response.Response;
import com.wiz.dev.wiztalk.utils.Utils;

public class Appmsgsrv8094Proxy implements InvocationHandler {

	private static final String TAG = "IMInterfaceProxy";

	public static Appmsgsrv8094 create(int timeout) {
//		ClientHttpRequestFactory requestFactory;
//		SimpleClientHttpRequestFactory schrf
//		CommonsClientHttpRequestFactory cchrf;
//		HttpComponentsClientHttpRequestFactory hcchrf
		
//		SimpleClientHttpRequestFactory schrf = new SimpleClientHttpRequestFactory();
//		schrf.setConnectTimeout(timeout);
//		schrf.setReadTimeout(timeout);
		
		HttpComponentsClientHttpRequestFactory hcchrf = new HttpComponentsClientHttpRequestFactory();
		hcchrf.setConnectTimeout(timeout);
		hcchrf.setReadTimeout(timeout);
		
		Appmsgsrv8094 myRestClient = new Appmsgsrv8094_(MyApplication.getInstance());
		myRestClient.getRestTemplate().setRequestFactory(hcchrf);
		
		Context context = MyApplication.getInstance();
		if (context != null) {
			myRestClient.setRootUrl(Utils.getURLAppmsgsrv8094(context, "/app/client/device"));
		}
		
		return create(myRestClient);
	}
	
	public static Appmsgsrv8094 create() {
		return create(8 * 1000);
	}
	
	private static Appmsgsrv8094 create(Appmsgsrv8094 object) {
		return (Appmsgsrv8094) Proxy.newProxyInstance(Appmsgsrv8094.class
				.getClassLoader(), new Class[] { Appmsgsrv8094.class },
				new Appmsgsrv8094Proxy(object));
	}

	private Appmsgsrv8094 mObj;

	public Appmsgsrv8094Proxy(Appmsgsrv8094 object) {
		mObj = object;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object result = method.invoke(mObj, args);

		handleTokenExpire(result);

		return result;
	}

	private void handleTokenExpire(Object result) {
		Log.d(TAG, "handleTokenExpire() isTokenExpire(result):" + isTokenExpire(result));
		if (isTokenExpire(result)) {
			MyApplication.getInstance().onReLogin();
		}
	}

	private boolean isTokenExpire(Object result) {
		if (result instanceof Response) {
			Response response = (Response) result;
			BaseResponse baseResponse = response.BaseResponse;
//			response.BaseResponse.Ret = BaseResponse.RET_ERROR_TOKEN;
			return baseResponse.Ret == BaseResponse.RET_ERROR_TOKEN;
		}

		return false;
	}

}
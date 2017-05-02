package com.wiz.dev.wiztalk.preference;

import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref(value=SharedPref.Scope.UNIQUE)
public interface CachePrefs {

	String token();
	
	String uid();
	
	String userName();
	
	String paasBaseUrl();
	
	int contentlength();
}

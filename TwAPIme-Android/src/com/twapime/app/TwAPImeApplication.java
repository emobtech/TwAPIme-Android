package com.twapime.app;

import com.twitterapime.rest.UserAccountManager;

import android.app.Application;

/**
 * @author ernandes@gmail.com
 */
public class TwAPImeApplication extends Application {
	/**
	 * 
	 */
	public static final String PREFS_NAME = "TwAPImePrefFile";
	
	/**
	 * 
	 */
	private UserAccountManager userAccountManager;
	
	/**
	 * @param userAccountMngr
	 */
	public void setUserAccountManager(UserAccountManager userAccountMngr) {
		userAccountManager = userAccountMngr;
	}
	
	/**
	 * @return
	 */
	public UserAccountManager getUserAccountManager() {
		return userAccountManager;
	}
}

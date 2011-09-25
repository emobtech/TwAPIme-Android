/*
 * TwAPImeApplication.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app;

import com.twitterapime.rest.FriendshipManager;
import com.twitterapime.rest.ListManager;
import com.twitterapime.rest.TweetER;
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

	/**
	 * @return
	 */
	public ListManager getListManager() {
		return ListManager.getInstance(userAccountManager);
	}

	/**
	 * @return
	 */
	public TweetER getTweetER() {
		return TweetER.getInstance(userAccountManager);
	}

	/**
	 * @return
	 */
	public FriendshipManager getFriendshipManager() {
		return FriendshipManager.getInstance(userAccountManager);
	}
}

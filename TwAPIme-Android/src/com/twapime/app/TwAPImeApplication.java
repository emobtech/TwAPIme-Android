/*
 * TwAPImeApplication.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.twitterapime.rest.FriendshipManager;
import com.twitterapime.rest.ListManager;
import com.twitterapime.rest.TweetER;
import com.twitterapime.rest.UserAccountManager;
import com.twitterapime.xauth.Token;

/**
 * @author ernandes@gmail.com
 */
public class TwAPImeApplication extends Application {
	/**
	 * 
	 */
	private static final String PREFS_NAME = "TwAPImePrefFile";
	
	/**
	 * 
	 */
	private static final String PREFS_KEY_TOKEN = "PREFS_KEY_TOKEN";
	
	/**
	 * 
	 */
	private static final String PREFS_KEY_TOKEN_SECRET =
		"PREFS_KEY_TOKEN_SECRET";
	
	/**
	 * 
	 */
	private UserAccountManager userAccountManager;

	/**
	 * 
	 */
	private Properties oauthProps;
	
	/**
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		//
		loadProperties();
	}
	
	/**
	 * 
	 */
	public void loadProperties() {
		InputStream resource = null;
		//
		try {
			Context ctx = getApplicationContext();
			//
		    resource = ctx.getResources().openRawResource(R.raw.oauth);
		    //
		    oauthProps = new Properties();
		    oauthProps.load(resource);
		    //
		    Log.d("twapime", "Loading Oauth.properties");
		} catch (Exception e) {
			Log.e("twapime", "Error by loading Oauth.properties");
		} finally {
			if (resource != null) {
				try {
					resource.close();
				} catch (IOException e) {}
			}
		}
	}
	
	/**
	 * @return
	 */
	public String getOAuthConsumerKey() {
		return oauthProps.getProperty("twapime.oauth.consumer_key");
	}
	
	/**
	 * @return
	 */
	public String getOAuthConsumerSecret() {
		return oauthProps.getProperty("twapime.oauth.consumer_secret");
	}

	/**
	 * @return
	 */
	public String getOAuthCallbackUrl() {
		return oauthProps.getProperty("twapime.oauth.callback_url");
	}
	
	/**
	 * @return
	 */
	public boolean isOAuthSingleAccessTokenEnabled() {
		return Boolean.valueOf(
			oauthProps.getProperty(
				"twapime.oauth.use_single_access_token", "false"));
	}

	/**
	 * @return
	 */
	public Token getAccessToken() {
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
		//
		if (prefs.getString(PREFS_KEY_TOKEN, null) == null) {
			if (isOAuthSingleAccessTokenEnabled()) {
	       		return new Token(
	       			oauthProps.getProperty("twapime.oauth.token"),
	       			oauthProps.getProperty("twapime.oauth.token_secret"));
			} else {
				return null;
			}
		} else {
       		return new Token(
       			prefs.getString(PREFS_KEY_TOKEN, null),
       			prefs.getString(PREFS_KEY_TOKEN_SECRET, null));
		}
	}
	
	/**
	 * @param token
	 */
	public void saveAccessToken(Token token) {
		SharedPreferences.Editor editor =
			getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
		//
		if (token != null) {
			editor.putString(PREFS_KEY_TOKEN, token.getToken());
			editor.putString(PREFS_KEY_TOKEN_SECRET, token.getSecret());
		} else {
			editor.remove(PREFS_KEY_TOKEN);
			editor.remove(PREFS_KEY_TOKEN_SECRET);
		}
		//
		editor.commit();
	}
	
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

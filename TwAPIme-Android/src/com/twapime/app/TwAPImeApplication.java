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
import android.content.res.Resources;
import android.util.Log;

import com.twitterapime.model.MetadataSet;
import com.twitterapime.rest.Credential;
import com.twitterapime.rest.FriendshipManager;
import com.twitterapime.rest.ListManager;
import com.twitterapime.rest.Timeline;
import com.twitterapime.rest.TweetER;
import com.twitterapime.rest.UserAccount;
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
	private UserAccountManager userAccountManager;

	/**
	 * 
	 */
	private Properties oauthProps;
	
	/**
	 * 
	 */
	private Properties gaProps;

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
			Resources res = ctx.getResources();
			//
		    resource = res.openRawResource(R.raw.oauth);
		    //
		    oauthProps = new Properties();
		    oauthProps.load(resource);
		    //
		    resource.close();
		    //
		    resource = res.openRawResource(R.raw.google_analytics);
		    //
		    gaProps = new Properties();
		    gaProps.load(resource);
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
		if (isOAuthSingleAccessTokenEnabled()) {
       		return new Token(
       			oauthProps.getProperty("twapime.oauth.token"),
       			oauthProps.getProperty("twapime.oauth.token_secret"),
       			oauthProps.getProperty("twapime.oauth.user_id"),
       			oauthProps.getProperty("twapime.oauth.screen_name"));
		} else {
			SharedPreferences prefs =
				getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
			//
			if (prefs.getString(PREFS_KEY_TOKEN, null) == null) {
				return null;
			} else {
				return Token.parse(prefs.getString(PREFS_KEY_TOKEN, null));
			}
		}
	}
	
	/**
	 * @return
	 */
	public String getGAAccountId() {
		return gaProps.getProperty("twapime.ga.account_id");
	}

	/**
	 * @return
	 */
	public int getGAInterval() {
		String value = gaProps.getProperty("twapime.ga.interval");
		//
		return value != null ? Integer.parseInt(value) : 20;
	}
	
	/**
	 * @return
	 */
	public Credential getCredential() {
		Token token = getAccessToken();
		//
		if (token != null) {
			return new Credential(
				getOAuthConsumerKey(), getOAuthConsumerSecret(), token);
		} else {
			return null;
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
			editor.putString(PREFS_KEY_TOKEN, token.toString());
		} else {
			editor.remove(PREFS_KEY_TOKEN);
		}
		//
		editor.commit();
	}
	
	/**
	 * @param user
	 * @return
	 */
	public boolean isLoggedUser(UserAccount user) {
        return getAccessToken().getUsername().equalsIgnoreCase(
        	user.getString(MetadataSet.USERACCOUNT_USER_NAME));
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
		if (userAccountManager == null) {
			Credential c = getCredential();
			//
			if (c != null) {
				userAccountManager = UserAccountManager.getInstance(c);
				//
				try {
					userAccountManager.verifyCredential();
				} catch (Exception e) {
					userAccountManager = null;
				}
			}
		}
		//
		return userAccountManager;
	}

	/**
	 * @return
	 */
	public ListManager getListManager() {
		return ListManager.getInstance(getUserAccountManager());
	}
	
	/**
	 * @return
	 */
	public Timeline getTimeline() {
		return Timeline.getInstance(getUserAccountManager());
	}

	/**
	 * @return
	 */
	public TweetER getTweetER() {
		return TweetER.getInstance(getUserAccountManager());
	}

	/**
	 * @return
	 */
	public FriendshipManager getFriendshipManager() {
		return FriendshipManager.getInstance(getUserAccountManager());
	}
}

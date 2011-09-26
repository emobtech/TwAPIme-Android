/*
 * AuthActivity.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.activity;

import impl.android.com.twitterapime.xauth.ui.WebViewOAuthDialogWrapper;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.twapime.app.R;
import com.twapime.app.TwAPImeApplication;
import com.twapime.app.service.AuthAsyncServiceCall;
import com.twapime.app.util.UIUtil;
import com.twitterapime.rest.Credential;
import com.twitterapime.rest.UserAccountManager;
import com.twitterapime.xauth.Token;
import com.twitterapime.xauth.ui.OAuthDialogListener;

/**
 * @author ernandes@gmail.com
 */
public class OAuthActivity extends Activity implements OAuthDialogListener {
	/**
	 * 
	 */
	private WebViewOAuthDialogWrapper loginWrapper;

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		WebView webView = new WebView(this);
		setContentView(webView);
		//
		TwAPImeApplication app = (TwAPImeApplication)getApplication();
		//
		loginWrapper = new WebViewOAuthDialogWrapper(webView);
		loginWrapper.setConsumerKey(app.getOAuthConsumerKey());
		loginWrapper.setConsumerSecret(app.getOAuthConsumerSecret());
		loginWrapper.setCallbackUrl(app.getOAuthCallbackUrl());
		loginWrapper.addOAuthListener(this);
		//
		loginWrapper.login();
	}

	/**
	 * @see com.twitterapime.xauth.ui.OAuthDialogListener#onAccessDenied(java.lang.String)
	 */
	@Override
	public void onAccessDenied(String message) {
		UIUtil.showMessage(this, message);
	}

	/**
	 * @see com.twitterapime.xauth.ui.OAuthDialogListener#onAuthorize(com.twitterapime.xauth.Token)
	 */
	@Override
	public void onAuthorize(Token token) {
		final TwAPImeApplication app = (TwAPImeApplication)getApplication();
		//
		new AuthAsyncServiceCall(this) {
			@Override
			protected void onPostRun(UserAccountManager result) {
				if (result != null) {
					//
					app.setUserAccountManager(result);
					app.saveAccessToken(result.getAccessToken());
					//
					startActivity(new Intent(getContext(), HomeActivity.class));
				} else {
					UIUtil.showMessage(
						getContext(), R.string.credentials_invalid);
					//
					loginWrapper.login();
				}
			};
			
			@Override
			protected void onFailedRun(Throwable result) {
				loginWrapper.login();
			};
		}.execute(
			new Credential(
				app.getOAuthConsumerKey(), 
				app.getOAuthConsumerSecret(), 
				token));
	}

	/**
	 * @see com.twitterapime.xauth.ui.OAuthDialogListener#onFail(java.lang.String, java.lang.String)
	 */
	@Override
	public void onFail(String message, String description) {
		UIUtil.showMessage(this, description);
	}
}

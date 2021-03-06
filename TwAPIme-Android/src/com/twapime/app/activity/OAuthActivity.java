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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.twapime.app.R;
import com.twapime.app.TwAPImeApplication;
import com.twapime.app.service.VerifyCredentialAsyncServiceCall;
import com.twapime.app.util.IOUtil;
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
	 * 
	 */
	private Runnable tryAgainError;
	
	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		//
		WebView webView = new WebView(this);
		webView.getSettings().setJavaScriptEnabled(true);
		//
		setContentView(webView);
		//
		setProgressBarIndeterminateVisibility(true);
		//
		webView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				setProgressBarIndeterminateVisibility(progress != 100);
			}
		});
		//
		TwAPImeApplication app = (TwAPImeApplication)getApplication();
		//
		loginWrapper = new WebViewOAuthDialogWrapper(webView);
		loginWrapper.setConsumerKey(app.getOAuthConsumerKey());
		loginWrapper.setConsumerSecret(app.getOAuthConsumerSecret());
		loginWrapper.setCallbackUrl(app.getOAuthCallbackUrl());
		loginWrapper.setOAuthListener(this);
		//
		final String body =
			"<html><body style='background-color:white'/></html>";
		//
		loginWrapper.setCustomSuccessPageHtml(body);
		loginWrapper.setCustomDeniedPageHtml(body);
		loginWrapper.setCustomErrorPageHtml(body);
		//
		login();
		//
		tryAgainError = new Runnable() {
			@Override
			public void run() {
				tryAgain(R.string.oauth_error_question);
			}
		};
	}
	
	/**
	 * 
	 */
	protected void login() {
		if (IOUtil.isOnline(this)) {
			loginWrapper.login();
		} else {
			tryAgain(R.string.network_access_failure);
		}
	}
	
	/**
	 * @param strId
	 */
	protected void tryAgain(int strId) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(strId);
		builder.setCancelable(false);
		builder.setPositiveButton(
			getString(R.string.yes), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				login();
			}
		});
		builder.setNegativeButton(
			getString(
				R.string.exit_app), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				finish();
			}
		});
		//
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.oauth, menu);
	    //
	    return true;
	}
	
	/**
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.menu_item_refresh:
	    	login();
	    	//
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

	/**
	 * @see com.twitterapime.xauth.ui.OAuthDialogListener#onAccessDenied(java.lang.String)
	 */
	@Override
	public void onAccessDenied(String message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				tryAgain(R.string.oauth_deny_question);
			}
		});
	}

	/**
	 * @see com.twitterapime.xauth.ui.OAuthDialogListener#onAuthorize(com.twitterapime.xauth.Token)
	 */
	@Override
	public void onAuthorize(Token token) {
		final TwAPImeApplication app = (TwAPImeApplication)getApplication();
		//
		new VerifyCredentialAsyncServiceCall(this) {
			@Override
			protected void onPostRun(UserAccountManager result) {
				if (result != null) {
					//
					app.setUserAccountManager(result);
					app.saveAccessToken(result.getAccessToken());
					//
					startActivity(new Intent(getContext(), HomeActivity.class));
				} else {
					runOnUiThread(tryAgainError);
				}
			};
			
			@Override
			protected void onFailedRun(Throwable result) {
				runOnUiThread(tryAgainError);
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
		runOnUiThread(tryAgainError);
	}
}

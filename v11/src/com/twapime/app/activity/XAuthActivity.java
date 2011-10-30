/*
 * AuthActivity.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.twapime.app.R;
import com.twapime.app.TwAPImeApplication;
import com.twapime.app.service.VerifyCredentialAsyncServiceCall;
import com.twapime.app.util.UIUtil;
import com.twapime.app.widget.SimpleTextWatcher;
import com.twitterapime.rest.Credential;
import com.twitterapime.rest.UserAccountManager;

/**
 * @author ernandes@gmail.com
 */
public class XAuthActivity extends Activity {
	/**
	 * 
	 */
	private EditText username;

	/**
	 * 
	 */
	private EditText password;

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.auth);
		//
		final Button btnSignIn = (Button)findViewById(R.id.auth_btn_sign_in);
		btnSignIn.setEnabled(false);
		btnSignIn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				signIn();
			}
		});
		//
		Button btnSignUp = (Button)findViewById(R.id.auth_btn_sign_up);
		btnSignUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(
					new Intent(
						Intent.ACTION_VIEW,
						Uri.parse("https://mobile.twitter.com/signup")));
			}
		});
		//
		username = (EditText)findViewById(R.id.auth_txtf_username);
		password = (EditText)findViewById(R.id.auth_txtf_password);
		//
		SimpleTextWatcher txtWatcher = new SimpleTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				btnSignIn.setEnabled(
					username.length() > 0 && password.length() > 0);
			}
		};
		//
		username.addTextChangedListener(txtWatcher);
		password.addTextChangedListener(txtWatcher);
	}
	
	/**
	 * 
	 */
	protected void signIn() {
		final TwAPImeApplication app = (TwAPImeApplication)getApplication();
		//
		Credential credential =
			new Credential(
				username.getText().toString(),
				password.getText().toString(),
				app.getOAuthConsumerKey(),
				app.getOAuthConsumerSecret());
		//
		new VerifyCredentialAsyncServiceCall(this) {
			@Override
			public void onPostRun(UserAccountManager result) {
				if (result != null) {
					app.setUserAccountManager(result);
					app.saveAccessToken(result.getAccessToken());
					//
					startActivity(new Intent(getContext(), HomeActivity.class));
				} else {
					UIUtil.showMessage(
						getContext(), R.string.invalid_credentials);
				}
			}
		}.execute(credential);
	}
}

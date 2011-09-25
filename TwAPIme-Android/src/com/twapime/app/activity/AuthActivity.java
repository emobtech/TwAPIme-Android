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
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.twapime.app.R;
import com.twapime.app.TwAPImeApplication;
import com.twapime.app.service.AuthAsyncServiceCall;
import com.twapime.app.util.UIUtil;
import com.twapime.app.widget.SimpleTextWatcher;
import com.twitterapime.rest.Credential;
import com.twitterapime.xauth.Token;

public class AuthActivity extends Activity {
	/**
	 * 
	 */
	public static final String PREFS_KEY_USERNAME = "PREFS_KEY_USERNAME";
	
	/**
	 * 
	 */
	public static final String PREFS_KEY_TOKEN = "PREFS_KEY_TOKEN";
	
	/**
	 * 
	 */
	public static final String PREFS_KEY_TOKEN_SECRET =
		"PREFS_KEY_TOKEN_SECRET";
	
	/**
	 * 
	 */
	public static final String CONSUMER_KEY = "KQlYF5kzKrBHm6s9gOyAVQ";
	
	/**
	 * 
	 */
	public static final String CONSUMER_SECRET = 
		"yv57uIvC8CMNo6NPyebwyDwbbw306xuXew4U5x81Ljw";
	
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
		Credential credential =
			new Credential(
				username.getText().toString(),
				password.getText().toString(),
				CONSUMER_KEY,
				CONSUMER_SECRET);
		//
		Token token =
			new Token(
				"55935824-P8omaqHxc9koLbiY7aJPo4TqHhTzdhU7xE9zRNc3I",
				"zacT3GkRQq9EPef82DgXfuZi9ULplwv1DX9TyGYYuA");
		credential = new Credential(CONSUMER_KEY, CONSUMER_SECRET, token);
		//
		new AuthAsyncServiceCall(this) {
			@Override
			public void onPostRun(Token result) {
				if (result != null) {
					saveCredentials(username.getText().toString(), result);
					startActivity(new Intent(getContext(), HomeActivity.class));
				} else {
					UIUtil.showMessage(
						getContext(), R.string.credentials_invalid);
				}
			}
		}.execute(credential);
	}
	
	/**
	 * @param username
	 * @param token
	 */
	protected void saveCredentials(String username, Token token) {
		SharedPreferences.Editor editor =
			getSharedPreferences(
				TwAPImeApplication.PREFS_NAME, MODE_PRIVATE).edit();
		//
		editor.putString(PREFS_KEY_USERNAME, username);
		editor.putString(PREFS_KEY_TOKEN, token.getToken());
		editor.putString(PREFS_KEY_TOKEN_SECRET, token.getSecret());
		//
		editor.commit();
	}
}

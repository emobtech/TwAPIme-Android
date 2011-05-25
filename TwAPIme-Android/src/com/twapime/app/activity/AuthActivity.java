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
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.twapime.app.R;
import com.twapime.app.TwAPImeApplication;
import com.twapime.app.util.UIUtil;
import com.twitterapime.rest.Credential;
import com.twitterapime.rest.UserAccountManager;
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
	public static final String CONSUMER_KEY = "gJT21KgpjtJvFrvkcsL5w";
	
	/**
	 * 
	 */
	public static final String CONSUMER_SECRET =
		"uRM24w1KnN5Hno3cgsf77gkeRNQSzd1atLFzLlsk";
	
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
		final EditText username =
			(EditText)findViewById(R.id.auth_txtf_username);
		final EditText password =
			(EditText)findViewById(R.id.auth_txtf_password);
		//
		TextWatcher txtWatcher = new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				btnSignIn.setEnabled(
					username.length() > 0 && password.length() > 0);
			}
		};
		//
		username.addTextChangedListener(txtWatcher);
		password.addTextChangedListener(txtWatcher);
		//
		username.setText("twiterapimetest");
		password.setText("password");
	}
	
	/**
	 * 
	 */
	protected void signIn() {
		final ProgressDialog progressDialog =
			ProgressDialog.show(
				this,
				"",
				getString(R.string.authenticating),
				false);
		//
		new Thread() {
			@Override
			public void run() {
				EditText username =
					(EditText)findViewById(R.id.auth_txtf_username);
				EditText password =
					(EditText)findViewById(R.id.auth_txtf_password);
				//
				Credential c =
					new Credential(
						username.getText().toString(),
						password.getText().toString(),
						CONSUMER_KEY,
						CONSUMER_SECRET);
				//
				Token token =
					new Token(
						"100090763-iEaRN7aUH589CnBEDVd4HxzIcEeYWkq8Yk0dSJyG",
						"OuV7m01mALwksfsNghk5r4Jo1LGhSIS54nR9rPeE");
				c =
					new Credential(
						username.getText().toString(),
						CONSUMER_KEY,
						CONSUMER_SECRET,
						token);
				//
				UserAccountManager uam = UserAccountManager.getInstance(c);
				//
				try {
					if (uam.verifyCredential()) {
						TwAPImeApplication app =
							(TwAPImeApplication)getApplication();
						app.setUserAccountManager(uam);
						//
						saveCredentials(
							username.getText().toString(),
							uam.getAccessToken());
						//
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								progressDialog.dismiss();
							}
						});
						//
						startActivity(
							new Intent(AuthActivity.this, HomeActivity.class));
					} else {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								progressDialog.dismiss();
								//
								UIUtil.showAlertDialog(
									AuthActivity.this,
									getString(R.string.credentials_invalid));
							}
						});
					}
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							progressDialog.dismiss();
							//
							UIUtil.showAlertDialog(AuthActivity.this, e);
						}
					});
				}
			};
		}.start();
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

package com.twapime.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
	public static final String CONSUMER_KEY = "KQlYF5kzKrBHm6s9gOyAVQ";
	
	/**
	 * 
	 */
	public static final String CONSUMER_SECRET =
		"yv57uIvC8CMNo6NPyebwyDwbbw306xuXew4U5x81Ljw";
	
	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.auth);
		//
		Button b = (Button)findViewById(R.id.auth_btn_sign_in);
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isFieldsFilledIn()) {
					signIn();
				} else {
					UIUtil.showAlertDialog(
						AuthActivity.this,
						getString(R.string.credentials_required));
				}
			}
		});
		//
		EditText username = (EditText)findViewById(R.id.auth_txtf_username);
		EditText password = (EditText)findViewById(R.id.auth_txtf_password);
		//
		username.setText("ernandesmjr");
		password.setText("password");
	}
	
	/**
	 * 
	 */
	public void signIn() {
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
						"55935824-Zlv4RPJqUtDNDUsJIReG8Epw7OEzKtii1sRkvRuN0",
						"tsasOzIZj03KW4xZoXOLQZf32AZSigbJrBWEvKBz8");
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
	public void saveCredentials(String username, Token token) {
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

	/**
	 * @return
	 */
	public boolean isFieldsFilledIn() {
		EditText txtf = (EditText)findViewById(R.id.auth_txtf_username);
		if (txtf.getText().toString().trim().length() == 0) {
			return false;
		}
		//
		txtf = (EditText)findViewById(R.id.auth_txtf_password);
		if (txtf.getText().toString().trim().length() == 0) {
			return false;
		}
		//
		return true;
	}
}

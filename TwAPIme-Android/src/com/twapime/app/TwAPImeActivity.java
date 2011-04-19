package com.twapime.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.twapime.app.activity.AuthActivity;
import com.twapime.app.activity.HomeActivity;
import com.twapime.app.util.UIUtil;
import com.twitterapime.rest.Credential;
import com.twitterapime.rest.UserAccountManager;
import com.twitterapime.xauth.Token;

/**
 * @author ernandes@gmail.com
 */
public class TwAPImeActivity extends Activity {
    /**
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        verifyExistentAccount();
    }
    
    /**
     * 
     */
    public void verifyExistentAccount() {
		SharedPreferences prefs =
			getSharedPreferences(TwAPImeApplication.PREFS_NAME, MODE_PRIVATE);
		//
        if (prefs.getString(AuthActivity.PREFS_KEY_TOKEN, null) == null) {
        	startActivity(new Intent(this, AuthActivity.class));
        } else {
        	Token token =
        		new Token(
        			prefs.getString(AuthActivity.PREFS_KEY_TOKEN, null),
        			prefs.getString(AuthActivity.PREFS_KEY_TOKEN_SECRET, null));
        	//
        	Credential c =
        		new Credential(
        			prefs.getString(AuthActivity.PREFS_KEY_USERNAME, null),
        			AuthActivity.CONSUMER_KEY,
        			AuthActivity.CONSUMER_SECRET,
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
					startActivity(new Intent(this, HomeActivity.class));
				} else {
					startActivity(new Intent(this, AuthActivity.class));
				}
			} catch (Exception e) {
				UIUtil.showAlertDialog(this, e);
			}
        }
    }
}

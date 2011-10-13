/*
 * TwAPImeActivity.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.twapime.app.activity.HomeActivity;
import com.twapime.app.activity.OAuthActivity;
import com.twapime.app.service.AuthAsyncServiceCall;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        verifyExistentAccount();
    }
    
    /**
     * 
     */
    protected void verifyExistentAccount() {
    	TwAPImeApplication app = (TwAPImeApplication)getApplication();
    	//
    	Token token = app.getAccessToken();
		//
        if (token == null) {
        	startActivity(new Intent(this, OAuthActivity.class));
        } else {
        	Credential credential =
        		new Credential(
        			app.getOAuthConsumerKey(), 
        			app.getOAuthConsumerSecret(),
        			token);
        	//
        	new AuthAsyncServiceCall(this) {
        		@Override
        		protected void onPostRun(UserAccountManager result) {
    				if (result != null) {
    					TwAPImeApplication app =
    						(TwAPImeApplication)getApplication();
    					app.setUserAccountManager(result);
    					//
    					startActivity(
    						new Intent(getContext(), HomeActivity.class));
    				} else {
    					startActivity(
    						new Intent(getContext(), OAuthActivity.class));
    				}
        		};
        		
        		@Override
        		protected void onFailedRun(Throwable result) {
        			startActivity(
        				new Intent(getContext(), OAuthActivity.class));
        		};
        	}.execute(credential);
        }
    }
}

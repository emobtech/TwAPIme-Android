/*
 * VerifyCredentialAsyncServiceCall.java
 * 24/09/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.service;

import java.io.IOException;

import android.app.Activity;

import com.twapime.app.R;
import com.twapime.app.util.GetAsyncServiceCall;
import com.twitterapime.rest.Credential;
import com.twitterapime.rest.UserAccountManager;
import com.twitterapime.search.LimitExceededException;

/**
 * @author ernandes@gmail.com
 */
public class VerifyCredentialAsyncServiceCall 
	extends GetAsyncServiceCall<Credential, Void, UserAccountManager> {
	/**
	 * @param context
	 */
	public VerifyCredentialAsyncServiceCall(Activity context) {
		super(context);
	}
	
	/**
	 * @see com.twapime.app.util.PostAsyncServiceCall#getProgressStringId()
	 */
	@Override
	public int getProgressStringId() {
		return R.string.signing_in_wait;
	}

	/**
	 * @see com.twapime.app.util.AsyncServiceCall#run(P[])
	 */
	@Override
	protected UserAccountManager run(Credential... params) throws IOException,
		LimitExceededException {
		UserAccountManager uam = UserAccountManager.getInstance(params[0]);
		//
		if (uam.verifyCredential()) {
			return uam;
		} else {
			return null;
		}
	}
}

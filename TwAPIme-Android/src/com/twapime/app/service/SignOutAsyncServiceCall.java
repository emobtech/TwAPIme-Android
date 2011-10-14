/*
 * SignOutAsyncServiceCall.java
 * 14/10/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

import com.twapime.app.R;
import com.twapime.app.util.GetAsyncServiceCall;
import com.twitterapime.rest.UserAccountManager;
import com.twitterapime.search.LimitExceededException;

/**
 * @author ernandes@gmail.com
 */
public class SignOutAsyncServiceCall 
	extends GetAsyncServiceCall<
		UserAccountManager, Void, List<UserAccountManager>> {
	/**
	 * @param context
	 */
	public SignOutAsyncServiceCall(Activity context) {
		super(context);
	}
	
	/**
	 * @see com.twapime.app.util.PostAsyncServiceCall#getProgressStringId()
	 */
	@Override
	public int getProgressStringId() {
		return R.string.signing_out_wait;
	}

	/**
	 * @see com.twapime.app.util.AsyncServiceCall#run(P[])
	 */
	@Override
	protected List<UserAccountManager> run(UserAccountManager... params)
		throws IOException, LimitExceededException {
		List<UserAccountManager> result = new ArrayList<UserAccountManager>();
		//
		for (UserAccountManager uam : params) {
			uam.signOut();
			result.add(uam);
		}
		//
		return result;
	}
}

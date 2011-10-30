/*
 * ReportSpamAsyncServiceCall.java
 * 25/09/2011
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
import com.twapime.app.TwAPImeApplication;
import com.twapime.app.util.GetAsyncServiceCall;
import com.twitterapime.rest.UserAccount;
import com.twitterapime.rest.UserAccountManager;
import com.twitterapime.search.LimitExceededException;

/**
 * @author ernandes@gmail.com
 */
public class GetUserAsyncServiceCall 
	extends GetAsyncServiceCall<UserAccount, Void, List<UserAccount>> {
	/**
	 * @param context
	 */
	public GetUserAsyncServiceCall(Activity context) {
		super(context);
	}
	
	/**
	 * @see com.twapime.app.util.PostAsyncServiceCall#getProgressStringId()
	 */
	@Override
	public int getProgressStringId() {
		return R.string.loading_wait;
	}

	/**
	 * @see com.twapime.app.util.AsyncServiceCall#run(P[])
	 */
	@Override
	protected List<UserAccount> run(UserAccount... params) throws IOException,
		LimitExceededException {
		TwAPImeApplication app =
			(TwAPImeApplication)getContext().getApplicationContext();
		UserAccountManager uam = app.getUserAccountManager();
		List<UserAccount> result = new ArrayList<UserAccount>();
		//
		for (UserAccount user : params) {
			result.add(uam.getUserAccount(user));
		}
		//
		return result;
	}
}

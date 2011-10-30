/*
 * GetSubscriptionsAsyncServiceCall.java
 * 23/09/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.service;

import java.io.IOException;

import android.app.Activity;

import com.twapime.app.TwAPImeApplication;
import com.twapime.app.util.GetAsyncServiceCall;
import com.twitterapime.rest.List;
import com.twitterapime.rest.ListManager;
import com.twitterapime.rest.UserAccount;
import com.twitterapime.search.LimitExceededException;

/**
 * @author ernandes@gmail.com
 */
public class GetSubscriptionsAsyncServiceCall 
	extends GetAsyncServiceCall<UserAccount, Void, List[]> {
	/**
	 * @param context
	 */
	public GetSubscriptionsAsyncServiceCall(Activity context) {
		super(context);
	}

	/**
	 * @see com.twapime.app.util.AsyncServiceCall#run(P[])
	 */
	@Override
	protected List[] run(UserAccount... params) throws IOException,
		LimitExceededException {
		TwAPImeApplication app =
			(TwAPImeApplication)getContext().getApplicationContext();
		ListManager lmgr = ListManager.getInstance(app.getUserAccountManager());
		//
		if (params != null) {
			return lmgr.getSubscriptions(params[0]);
		} else {
			return lmgr.getSubscriptions();
		}
	}
}

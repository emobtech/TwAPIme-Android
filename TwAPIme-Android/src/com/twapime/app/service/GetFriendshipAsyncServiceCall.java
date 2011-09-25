/*
 * GetFriendshipAsyncServiceCall.java
 * 24/09/2011
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
import com.twitterapime.rest.Friendship;
import com.twitterapime.rest.FriendshipManager;
import com.twitterapime.rest.UserAccount;
import com.twitterapime.search.LimitExceededException;

/**
 * @author ernandes@gmail.com
 */
public class GetFriendshipAsyncServiceCall 
	extends GetAsyncServiceCall<UserAccount, Void, List<Friendship>> {
	/**
	 * @param context
	 */
	public GetFriendshipAsyncServiceCall(Activity context) {
		super(context);
	}
	
	/**
	 * @see com.twapime.app.util.PostAsyncServiceCall#getProgressStringId()
	 */
	@Override
	public int getProgressStringId() {
		return R.string.loading_user_profile;
	}

	/**
	 * @see com.twapime.app.util.AsyncServiceCall#run(P[])
	 */
	@Override
	protected List<Friendship> run(UserAccount... params) throws IOException,
		LimitExceededException {
		TwAPImeApplication app = 
			(TwAPImeApplication)getContext().getApplicationContext();
		FriendshipManager fmgr = app.getFriendshipManager();
		List<Friendship> result = new ArrayList<Friendship>();
		//
		for (UserAccount user : params) {
			result.add(fmgr.getFriendship(user));
		}
		//
		return result;
	}
}

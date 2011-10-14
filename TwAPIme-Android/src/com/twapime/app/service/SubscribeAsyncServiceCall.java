/*
 * SubscribeAsyncServiceCall.java
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
import com.twapime.app.util.PostAsyncServiceCall;
import com.twitterapime.rest.ListManager;
import com.twitterapime.search.LimitExceededException;

/**
 * @author ernandes@gmail.com
 */
public class SubscribeAsyncServiceCall 
	extends PostAsyncServiceCall<
		com.twitterapime.rest.List, Void, List<com.twitterapime.rest.List>> {
	/**
	 * @param context
	 */
	public SubscribeAsyncServiceCall(Activity context) {
		super(context);
	}
	
	/**
	 * @see com.twapime.app.util.AsyncServiceCall#getProgressStringId()
	 */
	@Override
	public int getProgressStringId() {
		return R.string.subscribing_wait;
	}

	/**
	 * @see com.twapime.app.util.AsyncServiceCall#run(P[])
	 */
	@Override
	protected List<com.twitterapime.rest.List> run(
		com.twitterapime.rest.List... params) throws IOException,
		LimitExceededException {
		TwAPImeApplication app = 
			(TwAPImeApplication)getContext().getApplicationContext();
		ListManager lmgr = app.getListManager();
		List<com.twitterapime.rest.List> result =
			new ArrayList<com.twitterapime.rest.List>();
		//
		for (com.twitterapime.rest.List list : params) {
			result.add(lmgr.subscribe(list));
		}
		//
		return result;
	}
}

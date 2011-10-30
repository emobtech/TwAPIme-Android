/*
 * SendTweetAsyncServiceCall.java
 * 23/09/2011
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
import com.twitterapime.rest.TweetER;
import com.twitterapime.search.LimitExceededException;
import com.twitterapime.search.Tweet;

/**
 * @author ernandes@gmail.com
 */
public class SendTweetAsyncServiceCall 
	extends PostAsyncServiceCall<Tweet, Void, List<Tweet>> {
	/**
	 * @param context
	 */
	public SendTweetAsyncServiceCall(Activity context) {
		super(context);
	}
	
	/**
	 * @see com.twapime.app.util.PostAsyncServiceCall#getProgressStringId()
	 */
	@Override
	public int getProgressStringId() {
		return R.string.sending_dm_wait;
	}

	/**
	 * @see com.twapime.app.util.AsyncServiceCall#run(P[])
	 */
	@Override
	protected List<Tweet> run(Tweet... params) throws IOException,
		LimitExceededException {
		TwAPImeApplication app =
			(TwAPImeApplication)getContext().getApplicationContext();
		TweetER ter = TweetER.getInstance(app.getUserAccountManager());
		List<Tweet> result = new ArrayList<Tweet>();
		//
		for (Tweet tweet : params) {
			result.add(ter.send(tweet));
		}
		//
		return result;
	}
}

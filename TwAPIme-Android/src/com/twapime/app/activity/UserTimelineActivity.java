/*
 * UserTimelineActivity.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.activity;

import android.content.Intent;
import android.os.Bundle;

import com.twitterapime.model.MetadataSet;
import com.twitterapime.rest.UserAccount;
import com.twitterapime.search.Query;
import com.twitterapime.search.QueryComposer;

/**
 * @author ernandes@gmail.com
 */
public class UserTimelineActivity extends TimelineActivity {
	/**
	 * 
	 */
	static final String PARAM_KEY_USER = "PARAM_KEY_USER";

	/**
	 * 
	 */
	private String username;

	/**
	 * @see android.app.ActivityGroup#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		//
		UserAccount user =
			(UserAccount)intent.getExtras().getSerializable(PARAM_KEY_USER);
		//
		username = user.getString(MetadataSet.USERACCOUNT_USER_NAME);
		//
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * @see com.twapime.app.activity.TimelineActivity#refresh()
	 */
	@Override
	public void refresh() {
		super.refresh();
		//
		Query query = QueryComposer.screenName(username);
		//
		if (sinceID != null) {
			query = QueryComposer.append(query, sinceID);
		}
		//
		timeline.startGetUserTweets(query, this);
	}
}

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

import com.twitterapime.search.Query;
import com.twitterapime.search.QueryComposer;

/**
 * @author ernandes@gmail.com
 */
public class UserTimelineActivity extends TimelineActivity {
	/**
	 * 
	 */
	static final String PARAM_KEY_USERNAME = "PARAM_KEY_USERNAME";

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
		username = intent.getExtras().getString(PARAM_KEY_USERNAME);
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

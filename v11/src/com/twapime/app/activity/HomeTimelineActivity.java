/*
 * HomeTimelineActivity.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.activity;

import android.os.Bundle;

import com.twitterapime.search.QueryComposer;

/**
 * @author ernandes@gmail.com
 */
public class HomeTimelineActivity extends TimelineActivity {
	/**
	 * @see android.app.ActivityGroup#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		trackerPage = "/home_timeine";
		//
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * @see com.twapime.app.activity.TimelineActivity#refresh()
	 */
	@Override
	protected void refresh() {
		super.refresh();
		//
		timeline.startGetHomeTweets(
			QueryComposer.append(sinceID, QueryComposer.includeEntities()),
			this);
	}
}

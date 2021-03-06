/*
 * TweetSearchTimelineActivity.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import com.twitterapime.search.Query;
import com.twitterapime.search.QueryComposer;
import com.twitterapime.search.SearchDevice;

/**
 * @author ernandes@gmail.com
 */
public class TweetSearchTimelineActivity extends TimelineActivity {
	/**
	 * @see com.twapime.app.activity.TimelineActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		trackerPage = "/tweet_search_timeline";
		//
		super.onCreate(savedInstanceState);
	}

	/**
	 * @see com.twapime.app.activity.TimelineActivity#refresh()
	 */
	@Override
	protected void refresh() {
		super.refresh();
		tweets.clear();
		//
		String queryStr = getIntent().getStringExtra(SearchManager.QUERY);
		SearchDevice sd = SearchDevice.getInstance();
		Query query =
			QueryComposer.append(
				QueryComposer.containAll(queryStr),
				QueryComposer.includeEntities());
		//
		sd.startSearchTweets(query, this);
	}

	/**
	 * @see android.app.Activity#setIntent(android.content.Intent)
	 */
	@Override
	public void setIntent(Intent newIntent) {
		super.setIntent(newIntent);
		//
		refresh();
	}
}

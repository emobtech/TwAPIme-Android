package com.twapime.app.activity;

import android.app.SearchManager;
import android.os.Bundle;

import com.twitterapime.search.QueryComposer;
import com.twitterapime.search.SearchDevice;

/**
 * @author ernandes@gmail.com
 */
public class TweetSearchTimelineActivity extends TimelineActivity {
	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * @see com.twapime.app.activity.TimelineActivity#refresh()
	 */
	@Override
	public void refresh() {
		super.refresh();
		//
		String queryStr = getIntent().getStringExtra(SearchManager.QUERY);
		SearchDevice sd = SearchDevice.getInstance();
		//
		sd.startSearchTweets(QueryComposer.containAll(queryStr), this);
	}
}

/*
 * SearchHomeActivity.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

import com.twapime.app.R;

/**
 * @author ernandes@gmail.com
 */
public class SearchHomeActivity extends TabActivity {
	/**
	 * @see android.app.ActivityGroup#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		setContentView(R.layout.home);
		//
		Resources res = getResources();
		Intent intent = getIntent();
		String queryStr = null;
		//
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			queryStr = intent.getStringExtra(SearchManager.QUERY);
		}
		//
		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		//
		spec = tabHost.newTabSpec("tweets");
		spec.setIndicator(
			getString(R.string.tweets), res.getDrawable(R.drawable.chat));
		//
		intent = new Intent(this, TweetSearchTimelineActivity.class);
		intent.putExtra(SearchManager.QUERY, queryStr);
		//
		spec.setContent(intent);
		tabHost.addTab(spec);
		//
		spec = tabHost.newTabSpec("users");
		spec.setIndicator(
			getString(R.string.users), res.getDrawable(R.drawable.users));
		//
		intent = new Intent(this, UserSearchListActivity.class);
		intent.putExtra(SearchManager.QUERY, queryStr);
		//
		spec.setContent(intent);
		tabHost.addTab(spec);
	}

	/**
	 * @see android.app.Activity#onNewIntent(android.content.Intent)
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		Activity tab = getLocalActivityManager().getActivity("tweets");
		//
		if (tab != null) {
			tab.setIntent(intent);	
		}
		//
		tab = getLocalActivityManager().getActivity("users");
		//
		if (tab != null) {
			tab.setIntent(intent);	
		}
	}
}

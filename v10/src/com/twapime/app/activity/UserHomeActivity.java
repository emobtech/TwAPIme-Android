/*
 * UserHomeActivity.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.twapime.app.R;

/**
 * @author ernandes@gmail.com
 */
public class UserHomeActivity extends TabActivity {
	/**
	 * 
	 */
	static final String PARAM_KEY_USERNAME = "PARAM_KEY_USERNAME";
	
	/**
	 * @see android.app.ActivityGroup#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		setContentView(R.layout.home);
		//
		Intent intent = getIntent();
		//
		String username = intent.getExtras().getString(PARAM_KEY_USERNAME);
		//
		TabHost tabHost = getTabHost();
	    TabHost.TabSpec spec;
	    //
	    spec = tabHost.newTabSpec("profile");
	    spec.setIndicator(getString(R.string.profile), null);
	    //
		intent = new Intent(this, UserProfileActivity.class);
		intent.putExtra(UserProfileActivity.PARAM_KEY_USERNAME, username);
	    //
	    spec.setContent(intent);
	    tabHost.addTab(spec);
	    //
	    spec = tabHost.newTabSpec("tweets");
	    spec.setIndicator(getString(R.string.tweets), null);
	    //
		intent = new Intent(this, UserTimelineActivity.class);
		intent.putExtra(UserTimelineActivity.PARAM_KEY_USERNAME, username);
	    //
	    spec.setContent(intent);
	    tabHost.addTab(spec);
	    //
	    spec = tabHost.newTabSpec("friends");
	    spec.setIndicator(getString(R.string.friends), null);
	    //
		intent = new Intent(this, FriendListActivity.class);
		intent.putExtra(FriendListActivity.PARAM_KEY_USERNAME, username);
	    //
	    spec.setContent(intent);
	    tabHost.addTab(spec);
	    //
	    spec = tabHost.newTabSpec("followers");
	    spec.setIndicator(getString(R.string.followers), null);
	    //
		intent = new Intent(this, FollowerListActivity.class);
		intent.putExtra(FollowerListActivity.PARAM_KEY_USERNAME, username);
	    //
	    spec.setContent(intent);
	    tabHost.addTab(spec);
	}
}

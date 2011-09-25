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
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

import com.twapime.app.R;
import com.twitterapime.rest.UserAccount;

/**
 * @author ernandes@gmail.com
 */
public class UserHomeActivity extends TabActivity {
	/**
	 * 
	 */
	static final String PARAM_KEY_USER = "PARAM_KEY_USER";
	
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
		//
		UserAccount user =
			(UserAccount)intent.getExtras().getSerializable(PARAM_KEY_USER);
		//
		TabHost tabHost = getTabHost();
	    TabHost.TabSpec spec;
	    //
	    spec = tabHost.newTabSpec("profile");
	    spec.setIndicator(
	    	getString(R.string.profile), res.getDrawable(R.drawable.user));
	    //
		intent = new Intent(this, UserProfileActivity.class);
		intent.putExtra(UserProfileActivity.PARAM_KEY_USER, user);
	    //
	    spec.setContent(intent);
	    tabHost.addTab(spec);
	    //
	    spec = tabHost.newTabSpec("tweets");
	    spec.setIndicator(
	    	getString(R.string.tweets), res.getDrawable(R.drawable.chat));
	    //
		intent = new Intent(this, UserTimelineActivity.class);
		intent.putExtra(UserTimelineActivity.PARAM_KEY_USER, user);
	    //
	    spec.setContent(intent);
	    tabHost.addTab(spec);
	    //
	    spec = tabHost.newTabSpec("friends");
	    spec.setIndicator(
	    	getString(R.string.friends), res.getDrawable(R.drawable.users));
	    //
		intent = new Intent(this, FriendListActivity.class);
		intent.putExtra(FriendListActivity.PARAM_KEY_USER, user);
	    //
	    spec.setContent(intent);
	    tabHost.addTab(spec);
	    //
	    spec = tabHost.newTabSpec("followers");
	    spec.setIndicator(
	    	getString(R.string.followers), res.getDrawable(R.drawable.users));
	    //
		intent = new Intent(this, FollowerListActivity.class);
		intent.putExtra(FollowerListActivity.PARAM_KEY_USER, user);
	    //
	    spec.setContent(intent);
	    tabHost.addTab(spec);
	}
}

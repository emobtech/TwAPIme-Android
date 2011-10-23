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

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.twapime.app.R;
import com.twapime.app.TwAPImeApplication;
import com.twitterapime.model.MetadataSet;
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
	 * 
	 */
	private GoogleAnalyticsTracker tracker;
	
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
		TwAPImeApplication app = (TwAPImeApplication)getApplication();
		UserAccount user =
			(UserAccount)intent.getSerializableExtra(PARAM_KEY_USER);
		//
		setTitle("@" + user.getString(MetadataSet.USERACCOUNT_USER_NAME));
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
	    if (app.isLoggedUser(user)) {
		    spec = tabHost.newTabSpec("followers");
		    spec.setIndicator(
		    	getString(R.string.followers),
		    	res.getDrawable(R.drawable.users));
		    //
			intent = new Intent(this, FollowerListActivity.class);
			intent.putExtra(FollowerListActivity.PARAM_KEY_USER, user);
		    //
		    spec.setContent(intent);
		    tabHost.addTab(spec);
	    } else {
		    spec = tabHost.newTabSpec("lists");
		    spec.setIndicator(
		    	getString(R.string.lists),
		    	res.getDrawable(R.drawable.doc_lines));
		    //
			intent = new Intent(this, ListActivity.class);
			intent.putExtra(ListActivity.PARAM_KEY_USER, user);
		    //
		    spec.setContent(intent);
		    tabHost.addTab(spec);
	    }
		//
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.trackPageView("/profile_home");
	}
}

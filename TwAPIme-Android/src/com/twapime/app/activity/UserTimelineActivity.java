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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;

import com.twapime.app.TwAPImeApplication;
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
	 * 
	 */
	private boolean isLoggedUser;

	/**
	 * @see android.app.ActivityGroup#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		TwAPImeApplication app = (TwAPImeApplication)getApplication();
		Intent intent = getIntent();
		//
		UserAccount user =
			(UserAccount)intent.getExtras().getSerializable(PARAM_KEY_USER);
		//
		username = user.getString(MetadataSet.USERACCOUNT_USER_NAME);
		isLoggedUser = app.isLoggedUser(user);
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
		Query query =
			QueryComposer.append(
				QueryComposer.screenName(username),
				QueryComposer.includeRetweets());
		query = QueryComposer.append(query, QueryComposer.includeEntities());
		//
		if (sinceID != null) {
			query = QueryComposer.append(query, sinceID);
		}
		//
		timeline.startGetUserTweets(query, this);
	}
	
	/**
	 * @see com.twapime.app.activity.TimelineActivity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
		ContextMenuInfo menuInfo) {
		if (!isLoggedUser) {
			super.onCreateContextMenu(menu, v, menuInfo);	
		}
	}
}

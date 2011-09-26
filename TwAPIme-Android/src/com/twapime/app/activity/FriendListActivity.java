/*
 * FriendListActivity.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;

import com.twapime.app.TwAPImeApplication;
import com.twitterapime.model.Cursor;
import com.twitterapime.model.MetadataSet;
import com.twitterapime.rest.FriendshipManager;
import com.twitterapime.rest.UserAccount;
import com.twitterapime.rest.UserAccountManager;
import com.twitterapime.search.LimitExceededException;
import com.twitterapime.search.Query;
import com.twitterapime.search.QueryComposer;

/**
 * @author ernandes@gmail.com
 */
public class FriendListActivity extends UserListActivity {
	/**
	 * 
	 */
	static final String PARAM_KEY_USER = "PARAM_KEY_USER";
	
	/**
	 * 
	 */
	protected UserAccount user;
	
	/**
	 * @see com.twapime.app.activity.UserListActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		Intent intent = getIntent();
		//
		user = (UserAccount)intent.getExtras().getSerializable(PARAM_KEY_USER);
	}
	
	/**
	 * @see com.twapime.app.activity.UserListActivity#loadNextPage()
	 */
	@Override
	protected Cursor loadNextPage() throws IOException, 
		LimitExceededException {
		TwAPImeApplication app = (TwAPImeApplication)getApplication();
		UserAccountManager uam = app.getUserAccountManager();
		FriendshipManager fpm = app.getFriendshipManager();
		//
		if (nextPageQuery == null) {
			nextPageQuery = QueryComposer.cursor(-1);
		}
		nextPageQuery =
			QueryComposer.append(
				nextPageQuery,
				QueryComposer.screenName(
					user.getString(MetadataSet.USERACCOUNT_USER_NAME)));
		//
		Cursor cursorIds = fpm.getFriendsIDs(nextPageQuery);
		List<String> ids = new ArrayList<String>();
		//
		while (cursorIds.hasMoreElements()) {
			ids.add(cursorIds.nextElement().toString());
		}
		//
		Query nextLookupPageQuery =
			QueryComposer.append(
				QueryComposer.userIDs(ids.toArray(new String[ids.size()])),
				QueryComposer.skipStatus());
		//
		return new Cursor(
			uam.lookup(nextLookupPageQuery),
			cursorIds.getPreviousPageIndex(),
			cursorIds.getNextPageIndex());
	}
}

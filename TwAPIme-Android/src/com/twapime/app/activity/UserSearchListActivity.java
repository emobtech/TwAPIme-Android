/*
 * UserSearchListActivity.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.activity;

import java.io.IOException;

import android.app.SearchManager;
import android.content.Intent;

import com.twapime.app.TwAPImeApplication;
import com.twitterapime.model.Cursor;
import com.twitterapime.rest.UserAccountManager;
import com.twitterapime.search.LimitExceededException;
import com.twitterapime.search.QueryComposer;

/**
 * @author ernandes@gmail.com
 */
public class UserSearchListActivity extends UserListActivity {
	/**
	 * @see android.app.Activity#setIntent(android.content.Intent)
	 */
	@Override
	public void setIntent(Intent newIntent) {
		super.setIntent(newIntent);
		//
		reload();
	}

	/**
	 * @see com.twapime.app.activity.UserListActivity#loadNextPage()
	 */
	@Override
	protected Cursor loadNextPage() throws IOException, LimitExceededException {
		TwAPImeApplication app = (TwAPImeApplication)getApplication();
		UserAccountManager uam = app.getUserAccountManager();
		//
		String queryStr = getIntent().getStringExtra(SearchManager.QUERY);
		//
		return new Cursor(uam.search(QueryComposer.query(queryStr)), 0, 0);
	}
}

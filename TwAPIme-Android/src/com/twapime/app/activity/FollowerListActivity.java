/*
 * FollowerListActivity.java
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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.twapime.app.R;
import com.twapime.app.TwAPImeApplication;
import com.twapime.app.service.BlockAsyncServiceCall;
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
public class FollowerListActivity extends UserListActivity {
	/**
	 * 
	 */
	static final String PARAM_KEY_USER = "PARAM_KEY_USER";
	
	/**
	 * 
	 */
	private UserAccount user;
	
	/**
	 * 
	 */
	private boolean isLoggedUser;
	
	/**
	 * @see com.twapime.app.activity.UserListActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		TwAPImeApplication app = (TwAPImeApplication)getApplication();
		Intent intent = getIntent();
		//
		user = (UserAccount)intent.getExtras().getSerializable(PARAM_KEY_USER);
		isLoggedUser = app.isLoggedUser(user);
	}

	/**
	 * @see com.twapime.app.activity.UserListActivity#loadNextPage()
	 */
	@Override
	protected Cursor loadNextPage() throws IOException, LimitExceededException {
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
		Cursor cursorIds = fpm.getFollowersIDs(nextPageQuery);
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
	
	/**
	 * @param follower
	 */
	protected void block(final UserAccount follower) {
		new BlockAsyncServiceCall(this) {
			@Override
			protected void onPostRun(List<UserAccount> result) {
				users.remove(follower);
				adapter.notifyDataSetChanged();
			}
		}.execute(follower);
	}
	
	/**
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, 
		ContextMenuInfo menuInfo) {
		if (isLoggedUser) {
			getMenuInflater().inflate(R.menu.follower_list, menu);
		} else {
			super.onCreateContextMenu(menu, v, menuInfo);
		}
	}
	
	/**
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info =
			(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		//
	    switch (item.getItemId()) {
	    case R.id.menu_item_block:
	    	block(users.get(info.position));
	    	//
	        return true;
	    default:
	        return super.onContextItemSelected(item);
	    }
	}
}

/*
 * DirectMessageTimelineActivity.java
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.twapime.app.R;
import com.twitterapime.model.MetadataSet;
import com.twitterapime.search.Tweet;

/**
 * @author ernandes@gmail.com
 */
public class DirectMessageTimelineActivity extends TimelineActivity {
	/**
	 * 
	 */
	private int requestCount;
	
	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		trackerPage = "/dm_timeline";
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
		timeline.startGetDirectMessages(sinceID, true, this);
		timeline.startGetDirectMessages(sinceID, false, this);
		//
		requestCount = 0;
	}
	
	/**
	 * 
	 */
	protected void newDM() {
		startActivity(new Intent(this, NewDirectMessageActivity.class));
	    //
	    tracker.trackEvent(trackerPage, "new_dm", null, -1);
	}

	/**
	 * @see com.twapime.app.activity.TimelineActivity#viewTweet(com.twitterapime.search.Tweet)
	 */
	protected void viewTweet(Tweet tweet) {
		Intent intent = new Intent(this, NewDirectMessageActivity.class);
		intent.putExtra(
			NewDirectMessageActivity.PARAM_KEY_DM_RECIPIENT,
			tweet.getUserAccount().getString(
				MetadataSet.USERACCOUNT_USER_NAME));
		//
		startActivity(intent);
		//
		tracker.trackEvent(trackerPage, "view", null, -1);
	}

	/**
	 * @see com.twapime.app.activity.TimelineActivity#searchCompleted()
	 */
	@Override
	public void searchCompleted() {
		requestCount++;
		//
		if (requestCount == 2) {
			super.searchCompleted();
		}
	}
	
	/**
	 * @see com.twapime.app.activity.TimelineActivity#searchFailed(java.lang.Throwable)
	 */
	@Override
	public void searchFailed(Throwable exception) {
		requestCount++;
		//
		super.searchFailed(exception);
	}
	
	/**
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean result = super.onPrepareOptionsMenu(menu);
		//
		menu.findItem(R.id.menu_item_new_tweet).setTitle(R.string.new_dm);
		//
		return result;
	}
	
	/**
	 * @see com.twapime.app.activity.TimelineActivity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
		ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		//
		menu.removeItem(R.id.menu_item_retweet);
		menu.removeItem(R.id.menu_item_reply);
		menu.removeItem(R.id.menu_item_favorite);
		menu.removeItem(R.id.menu_item_comment);
	}
	
	/**
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.menu_item_new_tweet:
	    	newDM();
	    	//
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
}

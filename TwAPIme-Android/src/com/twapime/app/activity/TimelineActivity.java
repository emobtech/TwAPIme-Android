/*
 * TimelineActivity.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.twapime.app.R;
import com.twapime.app.TwAPImeApplication;
import com.twapime.app.service.FavoriteTweetAsyncServiceCall;
import com.twapime.app.service.RepostTweetAsyncServiceCall;
import com.twapime.app.util.UIUtil;
import com.twapime.app.widget.TimelineArrayAdapter;
import com.twitterapime.model.MetadataSet;
import com.twitterapime.rest.Timeline;
import com.twitterapime.search.Query;
import com.twitterapime.search.QueryComposer;
import com.twitterapime.search.SearchDeviceListener;
import com.twitterapime.search.Tweet;

/**
 * @author ernandes@gmail.com
 */
public class TimelineActivity extends ListActivity implements
	SearchDeviceListener {
	/**
	 * 
	 */
	static final int TWEET_COUNT = 200;

	/**
	 * 
	 */
	private ProgressDialog progressDialog;
	
	/**
	 * 
	 */
	private TimelineArrayAdapter adapter;

	/**
	 * 
	 */
	private Runnable notifyNewTweet;

	/**
	 * 
	 */
	protected List<Tweet> tweets;
	
	/**
	 * 
	 */
	protected Query sinceID;
	
	/**
	 * 
	 */
	protected Timeline timeline;
	
	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		tweets = new ArrayList<Tweet>();
		adapter = new TimelineArrayAdapter(this, R.layout.row_tweet, tweets);
		setListAdapter(adapter);
		//
		registerForContextMenu(getListView());
		//
		getListView().setOnItemClickListener(
			new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					viewTweet(tweets.get(position));
				}
			}
		);
		//
		notifyNewTweet = new Runnable() {
			@Override
			public void run() {
				adapter.notifyDataSetChanged();
				progressDialog.dismiss();
				//
				if (tweets.size() == 0) {
					UIUtil.showMessage(
						getApplicationContext(), R.string.no_tweet_found);
				}
			}
		};
		//
		TwAPImeApplication app = (TwAPImeApplication)getApplication();
		//
		timeline = Timeline.getInstance(app.getUserAccountManager());
		//
//		String lastTweetId = loadSavedLastTweetId();
//		if (lastTweetId != null) {
//			sinceID =
//				QueryComposer.append(
//					QueryComposer.sinceID(lastTweetId),
//					QueryComposer.count(TWEET_COUNT / 4));
//		} else {
		sinceID = QueryComposer.count(TWEET_COUNT / 4);
//		}
		//
		refresh();
	}
	
	/**
	 * 
	 */
	public void refresh() {
		progressDialog =
			ProgressDialog.show(
				this, "", getString(R.string.refreshing), false);
	}
	
	/**
	 * @param tweet
	 */
	public void viewTweet(Tweet tweet) {
		Intent intent = new Intent(this, ViewTweetActivity.class);
		intent.putExtra(ViewTweetActivity.PARAM_KEY_TWEET, tweet);
		//
		startActivity(intent);
	}
	
	/**
	 * 
	 */
	public void retweet(Tweet tweet) {
		new RepostTweetAsyncServiceCall(this).execute(tweet);
	}
	
	/**
	 * @param tweet
	 */
	public void comment(Tweet tweet) {
		String content =
			"RT @" +
			tweet.getUserAccount().getString(MetadataSet.USERACCOUNT_USER_NAME)+
			": " +
			tweet.getString(MetadataSet.TWEET_CONTENT);
		//
		Intent intent = new Intent(this, NewTweetActivity.class);
		intent.putExtra(NewTweetActivity.PARAM_KEY_TWEET_CONTENT, content);
		//
		startActivity(intent);
	}
	
	/**
	 * @param tweet
	 */
	public void newDM(Tweet tweet) {
		String repicient =
			tweet.getUserAccount().getString(MetadataSet.USERACCOUNT_USER_NAME);
		//
		Intent intent = new Intent(this, NewDirectMessageActivity.class);
		intent.putExtra(
			NewDirectMessageActivity.PARAM_KEY_DM_RECIPIENT, repicient);
		//
		startActivity(intent);
	}
	
	/**
	 * @param tweet
	 */
	public void reply(Tweet tweet) {
		String username =
			tweet.getUserAccount().getString(MetadataSet.USERACCOUNT_USER_NAME);
		//
		Intent intent = new Intent(this, NewTweetActivity.class);
		//
		intent.putExtra(NewTweetActivity.PARAM_KEY_REPLY_TWEET, tweet);
		intent.putExtra(
			NewTweetActivity.PARAM_KEY_TWEET_CONTENT, "@" + username);
		//
		startActivity(intent);
	}
	
	/**
	 * @param tweet
	 */
	public void favorite(Tweet tweet) {
		FavoriteTweetAsyncServiceCall favCall =
			new FavoriteTweetAsyncServiceCall(this);
		favCall.setProgressStringId(
			isFavorite(tweet) ? R.string.unfavoriting : R.string.favoriting);
		//
		favCall.execute(tweet);
	}

	/**
	 * @see com.twitterapime.search.SearchDeviceListener#searchCompleted()
	 */
	@Override
	public void searchCompleted() {
		Collections.sort(tweets, new Comparator<Tweet>() {
			@Override
			public int compare(Tweet t1, Tweet t2) {
				long pd1 = Long.parseLong(t1.getString(MetadataSet.TWEET_ID));
				long pd2 = Long.parseLong(t2.getString(MetadataSet.TWEET_ID));
				//
				if (pd1 < pd2) {
					return 1;
				} else if (pd1 > pd2) {
					return -1;
				} else {
					return 0;
				}
			}
		});
		if (tweets.size() > 0) {
			String lastTweetId = tweets.get(0).getString(MetadataSet.TWEET_ID);
			//
			sinceID =
				QueryComposer.append(
					QueryComposer.sinceID(lastTweetId),
					QueryComposer.count(TWEET_COUNT));
			//
//			saveLastTweetId(lastTweetId);
		}
		//
		runOnUiThread(notifyNewTweet);
	}

	/**
	 * @see com.twitterapime.search.SearchDeviceListener#searchFailed(java.lang.Throwable)
	 */
	@Override
	public void searchFailed(final Throwable exception) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressDialog.dismiss();
				//
				UIUtil.showMessage(TimelineActivity.this, exception);
			}
		});
	}

	/**
	 * @see com.twitterapime.search.SearchDeviceListener#tweetFound(com.twitterapime.search.Tweet)
	 */
	@Override
	public void tweetFound(Tweet tweet) {
		tweets.add(tweet);
	}
	
	/**
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (getParent() == null) {
			getMenuInflater().inflate(R.menu.timeline, menu);
			//
			return true;
		} else {
			getParent().getMenuInflater().inflate(R.menu.timeline, menu);
			//
			return super.onCreateOptionsMenu(menu);
		}
	}
	
	/**
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.menu_item_refresh:
	    	refresh();
	    	//
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	/**
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
		ContextMenuInfo menuInfo) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.tweet, menu);
		//
		AdapterView.AdapterContextMenuInfo info =
			(AdapterView.AdapterContextMenuInfo)menuInfo;
		//
		menu.findItem(R.id.menu_item_favorite).setTitle(
			isFavorite(tweets.get(info.position))
				? R.string.unfavorite : R.string.favorite);
	}
	
	/**
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info =
			(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		//
		Tweet tweet = tweets.get(info.position);
		//
	    switch (item.getItemId()) {
	    case R.id.menu_item_retweet:
	    	retweet(tweet);
	    	//
	        return true;
	    case R.id.menu_item_comment:
	    	comment(tweet);
	    	//
	        return true;
	    case R.id.menu_item_new_dm:
	    	newDM(tweet);
	    	//
	        return true;
	    case R.id.menu_item_reply:
	    	reply(tweet);
	    	//
	        return true;
	    case R.id.menu_item_favorite:
	    	favorite(tweet);
	    	//
	        return true;
	    default:
	        return super.onContextItemSelected(item);
	    }
	}
	
//	/**
//	 * @param tweetId
//	 */
//	protected void saveLastTweetId(String tweetId) {
//		SharedPreferences.Editor editor =
//			getSharedPreferences(
//				TwAPImeApplication.PREFS_NAME, MODE_PRIVATE).edit();
//		//
//		editor.putString(getClass().getName(), tweetId);
//		//
//		editor.commit();
//	}
	
//	/**
//	 * @return
//	 */
//	protected String loadSavedLastTweetId() {
//		SharedPreferences prefs =
//			getSharedPreferences(TwAPImeApplication.PREFS_NAME, MODE_PRIVATE);
//		//
//		return prefs.getString(getClass().getName(), null);
//		return null;
//	}
	
	/**
	 * @param tweet
	 * @return
	 */
	private boolean isFavorite(Tweet tweet) {
		return tweet.getBoolean(MetadataSet.TWEET_FAVOURITE);
	}
}

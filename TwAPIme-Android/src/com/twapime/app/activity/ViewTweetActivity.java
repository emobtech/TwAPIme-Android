/*
 * ViewTweetActivity.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.twapime.app.R;
import com.twapime.app.service.FavoriteTweetAsyncServiceCall;
import com.twapime.app.service.RepostTweetAsyncServiceCall;
import com.twapime.app.util.AsyncImageLoader;
import com.twapime.app.util.DateUtil;
import com.twapime.app.widget.ImageViewCallback;
import com.twitterapime.model.MetadataSet;
import com.twitterapime.rest.UserAccount;
import com.twitterapime.search.Tweet;
import com.twitterapime.search.TweetEntity;

/**
 * @author ernandes@gmail.com
 */
public class ViewTweetActivity extends Activity {
	/**
	 * 
	 */
	static final String PARAM_KEY_TWEET = "PARAM_KEY_TWEET";
	
	/**
	 * 
	 */
	private Tweet tweet;
	
	/**
	 * 
	 */
	private boolean isFavorite;
	
	/**
	 * 
	 */
	private GoogleAnalyticsTracker tracker;
	
	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		setContentView(R.layout.view_tweet);
		//
		final Button btn = (Button)findViewById(R.id.view_tweet_btn_profile);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				viewUserProfile();
			}
		});
		tweet = (Tweet)getIntent().getExtras().getSerializable(PARAM_KEY_TWEET);
		isFavorite = tweet.getBoolean(MetadataSet.TWEET_FAVOURITE);
		//
		displayTweet();
		//
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.trackPageView("/tweet");
	}
	
	/**
	 * 
	 */
	protected void retweet() {
		new RepostTweetAsyncServiceCall(this) {
			@Override
			protected void onPostRun(List<Tweet> result) {
				tweet = result.get(0);
				displayTweet();
				//
				tracker.trackEvent("/tweet", "retweet", null, -1);
			};
		}.execute(tweet);
	}
	
	/**
	 * 
	 */
	protected void comment() {
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
		//
		tracker.trackEvent("/tweet", "comment", null, -1);
	}
	
	/**
	 * 
	 */
	protected void reply() {
		Intent intent = new Intent(this, NewTweetActivity.class);
		intent.putExtra(NewTweetActivity.PARAM_KEY_REPLY_TWEET, tweet);
		//
		startActivity(intent);
		//
		tracker.trackEvent("/tweet", "reply", null, -1);
	}
	
	/**
	 * 
	 */
	protected void favorite() {
		FavoriteTweetAsyncServiceCall favCall =
			new FavoriteTweetAsyncServiceCall(this) {
			@Override
			protected void onPostRun(List<Tweet> result) {
				tracker.trackEvent(
					"/tweet", isFavorite ? "unfavorite" : "favorite", null, -1);
				//
				isFavorite = !isFavorite;
			}
		};
		favCall.setProgressStringId(
			isFavorite ? R.string.unfavoriting_wait : R.string.favoriting_wait);
		//
		favCall.execute(tweet);
	}
	
	/**
	 * 
	 */
	protected void viewUserProfile() {
		UserAccount account = tweet.getUserAccount();
		//
		if (tweet.getRepostedTweet() != null) {
			account = tweet.getRepostedTweet().getUserAccount();
		}
		//
		Intent intent = new Intent(this, UserHomeActivity.class);
		intent.putExtra(UserHomeActivity.PARAM_KEY_USER, account);
		//
		startActivity(intent);
		tracker.trackEvent("/tweet", "profile", null, -1);
	}
	
	/**
	 * 
	 */
	protected void displayTweet() {
		Tweet dTweet = tweet;
		TextView txtv = (TextView)findViewById(R.id.view_tweet_txtv_reply);
		txtv.setVisibility(View.VISIBLE);
		//
		if (dTweet.getRepostedTweet() != null) { //retweeted?
	    	String username =
	    		dTweet.getUserAccount().getString(MetadataSet.USERACCOUNT_NAME);
	    	//
	    	txtv.setText(getString(R.string.retweeted_by, username));
	        //
	    	dTweet = tweet.getRepostedTweet();
		} else {
			if (dTweet.isEmpty(MetadataSet.TWEET_IN_REPLY_TO_TWEET_ID)) {
				txtv.setVisibility(View.GONE);
	    	} else {
	    		String username = "";
	    		//
	    		if (dTweet.getEntity() != null) {
	    			TweetEntity[] entities = dTweet.getEntity().getMentions();
	    			if (entities.length > 0) {
	    				username =
	    					entities[0].getString(
	    						MetadataSet.TWEETENTITY_USERACCOUNT_NAME);
	    			}
	    		}
	    		//
	    		txtv.setText(getString(R.string.in_reply_to, username));
	    	}
		}
		//
		UserAccount ua = dTweet.getUserAccount();
		//
		txtv = (TextView)findViewById(R.id.view_tweet_txtv_name);
		if (ua != null) {
			txtv.setText(ua.getString(MetadataSet.USERACCOUNT_NAME));	
		} else {
			txtv.setText("");
		}
		//
		txtv = (TextView)findViewById(R.id.view_tweet_txtv_username);
		if (ua != null) {
			txtv.setText("@" + ua.getString(MetadataSet.USERACCOUNT_USER_NAME));	
		} else {
			txtv.setText("");
		}
		//
		txtv = (TextView)findViewById(R.id.view_tweet_txtv_content);
		txtv.setText(dTweet.getString(MetadataSet.TWEET_CONTENT));	
		//
		txtv = (TextView)findViewById(R.id.view_tweet_txtv_app);
		txtv.setText(dTweet.getString(MetadataSet.TWEET_SOURCE));	
		//
		txtv = (TextView)findViewById(R.id.view_tweet_txtv_time);
		try {
			txtv.setText(
				DateUtil.formatTweetDate(
	        		Long.parseLong(
	        			dTweet.getString(MetadataSet.TWEET_PUBLISH_DATE)),
	            		this));
		} catch (NumberFormatException e) {
			txtv.setText("");
		}
		//
		ImageView imgV = (ImageView)findViewById(R.id.view_tweet_imgv_avatar);
		//
		String imgUrl = null;
		Drawable cachedImage = null;
		//
		if (ua != null) {
			imgUrl = ua.getString(MetadataSet.USERACCOUNT_PICTURE_URI);	
		}
	    //
	    if (imgUrl != null) {
	    	imgV.setTag(imgUrl);
	        cachedImage =
	        	AsyncImageLoader.getInstance(this).loadDrawable(
	        		imgUrl, new ImageViewCallback(null, getCurrentFocus()));
	    }
	    //
	    if (cachedImage == null) {
	    	cachedImage = getResources().getDrawable(R.drawable.icon);
	    }
	    //
	    imgV.setImageDrawable(cachedImage);
	}

	/**
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.tweet, menu);
	    //
	    return true;
	}

	/**
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.menu_item_favorite).setVisible(!isFavorite);
		menu.findItem(R.id.menu_item_unfavorite).setVisible(isFavorite);
		//
		return super.onPrepareOptionsMenu(menu);
	}
	
	/**
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.menu_item_retweet:
	    	retweet();
	    	//
	        return true;
	    case R.id.menu_item_comment:
	    	comment();
	    	//
	        return true;
	    case R.id.menu_item_reply:
	    	reply();
	    	//
	        return true;
	    case R.id.menu_item_favorite:
	    case R.id.menu_item_unfavorite:
	    	favorite();
	    	//
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
}

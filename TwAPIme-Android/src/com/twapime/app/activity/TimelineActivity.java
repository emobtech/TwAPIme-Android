package com.twapime.app.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.twapime.app.R;
import com.twapime.app.TwAPImeApplication;
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
	private ProgressDialog progressDialog;
	
	/**
	 * 
	 */
	private TimelineArrayAdapter adapter;

	/**
	 * 
	 */
	private List<Tweet> tweets;
	
	/**
	 * 
	 */
	private Runnable notifyNewTweet;
	
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
		adapter = new TimelineArrayAdapter(this, R.layout.tweet_row, tweets);
		setListAdapter(adapter);
		//
		notifyNewTweet = new Runnable() {
			@Override
			public void run() {
				adapter.notifyDataSetChanged();
				progressDialog.dismiss();
			}
		};
		//
		TwAPImeApplication app = (TwAPImeApplication)getApplication();
		//
		timeline = Timeline.getInstance(app.getUserAccountManager());
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
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
		Intent data) {
		if (resultCode == HomeActivity.NEW_TWEET_RESULT) {
			refresh();
		}
	}

	/**
	 * @see com.twitterapime.search.SearchDeviceListener#searchCompleted()
	 */
	@Override
	public void searchCompleted() {
		runOnUiThread(notifyNewTweet);
		//
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
		sinceID =
			QueryComposer.sinceID(
				tweets.get(0).getString(MetadataSet.TWEET_ID));
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
				UIUtil.showAlertDialog(TimelineActivity.this, exception);
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
}

/*
 * ViewTweetActivity.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.twapime.app.R;
import com.twapime.app.TwAPImeApplication;
import com.twapime.app.util.AsyncImageLoader;
import com.twapime.app.util.DateUtil;
import com.twapime.app.util.UIUtil;
import com.twapime.app.widget.ImageViewCallback;
import com.twitterapime.model.MetadataSet;
import com.twitterapime.rest.TweetER;
import com.twitterapime.rest.UserAccount;
import com.twitterapime.search.Tweet;

/**
 * @author ernandes@gmail.com
 */
public class ViewTweetActivity extends Activity {
	/**
	 * 
	 */
	static final String PARAM_KEY_TWEET_ID = "PARAM_KEY_TWEET_ID";
	
	/**
	 * 
	 */
	private TweetER ter;
	
	/**
	 * 
	 */
	private Tweet tweet;
	
	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		setContentView(R.layout.view_tweet);
		//
		TwAPImeApplication app = (TwAPImeApplication)getApplication();
		ter = TweetER.getInstance(app.getUserAccountManager());
		//
		final Button btn = (Button)findViewById(R.id.view_tweet_btn_profile);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				viewUserProfile();
			}
		});
		//
		displayTweet();
		//
		loadTweet(getIntent().getExtras().getString(PARAM_KEY_TWEET_ID));
	}
	
	/**
	 * @param tweetID
	 */
	public void loadTweet(final String tweetID) {
		final ProgressDialog progressDialog =
			ProgressDialog.show(
				this, "", getString(R.string.loading_tweet), false);
		//
		new Thread() {
			@Override
			public void run() {
				try {
					tweet = ter.findByID(tweetID);
					//
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							displayTweet();
							//
							progressDialog.dismiss();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							progressDialog.dismiss();
							//
							UIUtil.showAlertDialog(ViewTweetActivity.this, e);
						}
					});
				}
			};
		}.start();
	}
	
	/**
	 * 
	 */
	public void retweet() {
		final ProgressDialog progressDialog =
			ProgressDialog.show(
				this, "", getString(R.string.retweeting), false);
		//
		new Thread() {
			@Override
			public void run() {
				try {
					ter.repost(tweet);
					//
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							progressDialog.dismiss();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							progressDialog.dismiss();
							//
							UIUtil.showAlertDialog(ViewTweetActivity.this, e);
						}
					});
				}
			};
		}.start();
	}
	
	/**
	 * 
	 */
	public void comment() {
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
	 * 
	 */
	public void newDM() {
		String recipient =
			tweet.getUserAccount().getString(MetadataSet.USERACCOUNT_USER_NAME);
		//
		Intent intent = new Intent(this, NewDirectMessageActivity.class);
		//
		intent.putExtra(
			NewDirectMessageActivity.PARAM_KEY_DM_RECIPIENT, recipient);
		//
		startActivity(intent);
	}
	
	/**
	 * 
	 */
	public void reply() {
		String tweetID = tweet.getString(MetadataSet.TWEET_ID);
		String username =
			tweet.getUserAccount().getString(MetadataSet.USERACCOUNT_USER_NAME);
		//
		Intent intent = new Intent(this, NewTweetActivity.class);
		//
		intent.putExtra(
			NewTweetActivity.PARAM_KEY_REPLY_TWEET_ID, tweetID);
		intent.putExtra(
			NewTweetActivity.PARAM_KEY_REPLY_USERNAME, username);
		intent.putExtra(
			NewTweetActivity.PARAM_KEY_TWEET_CONTENT, "@" + username);
		//
		startActivity(intent);
	}
	
	/**
	 * 
	 */
	public void favorite() {
		final ProgressDialog progressDialog =
			ProgressDialog.show(
				this, "",
				getString(
					isFavoriteTweet()
						? R.string.unfavoriting : R.string.favoriting),
				false);
		//
		new Thread() {
			@Override
			public void run() {
				try {
					ter.favorite(tweet);
					//
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							progressDialog.dismiss();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							progressDialog.dismiss();
							//
							UIUtil.showAlertDialog(ViewTweetActivity.this, e);
						}
					});
				}
			};
		}.start();
	}
	
	/**
	 * 
	 */
	protected void viewUserProfile() {
		UserAccount ua = tweet.getUserAccount();
		//
		Intent intent = new Intent(this, UserHomeActivity.class);
		intent.putExtra(
			UserHomeActivity.PARAM_KEY_USERNAME,
			ua.getString(MetadataSet.USERACCOUNT_USER_NAME));
		//
		startActivity(intent);
	}
	
	/**
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean result = super.onPrepareOptionsMenu(menu);
		//
		menu.findItem(R.id.menu_item_favorite).setTitle(
			isFavoriteTweet() ? R.string.unfavorite : R.string.favorite);	
		//
		return result;
	}
	
	/**
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.view_tweet, menu);
	    //
	    return true;
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
	    case R.id.menu_item_new_dm:
	    	newDM();
	    	//
	        return true;
	    case R.id.menu_item_reply:
	    	reply();
	    	//
	        return true;
	    case R.id.menu_item_favorite:
	    	favorite();
	    	//
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	/**
	 * 
	 */
	public void displayTweet() {
		UserAccount ua = null;
		//
		if (tweet != null) {
			ua = tweet.getUserAccount();
		}
		//
		TextView txtv = (TextView)findViewById(R.id.view_tweet_txtv_name);
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
		if (tweet != null) {
			txtv.setText(tweet.getString(MetadataSet.TWEET_CONTENT));	
		} else {
			txtv.setText("");
		}
		//
		txtv = (TextView)findViewById(R.id.view_tweet_txtv_app);
		if (tweet != null) {
			txtv.setText(tweet.getString(MetadataSet.TWEET_SOURCE));	
		} else {
			txtv.setText("");
		}
		//
		txtv = (TextView)findViewById(R.id.view_tweet_txtv_time);
		if (tweet != null) {
			txtv.setText(
				DateUtil.formatTweetDate(
	        		Long.parseLong(
	            		tweet.getString(MetadataSet.TWEET_PUBLISH_DATE)),
	            		this));
		} else {
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
	 * @return
	 */
	private boolean isFavoriteTweet() {
		String favorite = tweet.getString(MetadataSet.TWEET_FAVOURITE);
		//
		return favorite != null && "true".equals(favorite);
	}
}

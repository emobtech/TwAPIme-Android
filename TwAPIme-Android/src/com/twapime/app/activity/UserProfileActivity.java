/*
 * UserProfileActivity.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.twapime.app.R;
import com.twapime.app.service.BlockAsyncServiceCall;
import com.twapime.app.service.FollowAsyncServiceCall;
import com.twapime.app.service.GetFriendshipAsyncServiceCall;
import com.twapime.app.service.GetUserAsyncServiceCall;
import com.twapime.app.service.ReportSpamAsyncServiceCall;
import com.twapime.app.service.UnblockAsyncServiceCall;
import com.twapime.app.service.UnfollowAsyncServiceCall;
import com.twapime.app.util.AsyncImageLoader;
import com.twapime.app.util.DateUtil;
import com.twapime.app.widget.ImageViewCallback;
import com.twitterapime.model.MetadataSet;
import com.twitterapime.rest.Friendship;
import com.twitterapime.rest.UserAccount;
import com.twitterapime.search.Tweet;

/**
 * @author ernandes@gmail.com
 */
public class UserProfileActivity extends Activity {
	/**
	 * 
	 */
	private static final int REQUEST_EDIT_USER = 0;
	
	/**
	 * 
	 */
	static final String PARAM_KEY_USER = "PARAM_KEY_USER";
	
	/**
	 * 
	 */
	static final String PARAM_KEY_IS_LOGGED_USER = "PARAM_KEY_IS_LOGGED_USER";

	/**
	 * 
	 */
	private UserAccount user;
	
	/**
	 * 
	 */
	private boolean isFollowing;
	
	/**
	 * 
	 */
	private boolean isBlocking;
	
	/**
	 * 
	 */
	private boolean isLoggedUser;

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		setContentView(R.layout.user_profile);
		//
		Intent intent = getIntent();
		//
		user = (UserAccount)intent.getSerializableExtra(PARAM_KEY_USER);
		isLoggedUser = intent.getBooleanExtra(PARAM_KEY_IS_LOGGED_USER, false);
		//
		loadUserProfile();
		loadUserFriendship();
	}
	
	/**
	 * 
	 */
	public void loadUserProfile() {
		if (user.getObject(MetadataSet.USERACCOUNT_CREATE_DATE) == null) {
			new GetUserAsyncServiceCall(this) {
				@Override
				protected void onPostRun(List<UserAccount> result) {
					user = result.get(0);
					displayUserProfile();
				}
			}.execute(user); //load all user data.
		} else {
			displayUserProfile();
		}
	}
	
	/**
	 * 
	 */
	public void loadUserFriendship() {
		new GetFriendshipAsyncServiceCall(this) {
			protected void onPostRun(List<Friendship> result) {
				Friendship f = result.get(0).getSource();
				//
				isFollowing = f.getBoolean(MetadataSet.FRIENDSHIP_FOLLOWING);
				isBlocking = f.getBoolean(MetadataSet.FRIENDSHIP_BLOCKING);
			};
		}.execute(user);
	}
	
	/**
	 * 
	 */
	public void displayUserProfile() {
		TextView txtv = (TextView)findViewById(R.id.user_profile_txtv_name);
		txtv.setText(user.getString(MetadataSet.USERACCOUNT_NAME));	
		//
		txtv = (TextView)findViewById(R.id.user_profile_txtv_username);
		txtv.setText("@" + user.getString(MetadataSet.USERACCOUNT_USER_NAME));	
		//
		txtv = (TextView)findViewById(R.id.user_profile_txtv_last_tweet);
		Tweet tweet = user.getLastTweet();
		//
		if (tweet != null) {
			txtv.setText(tweet.getString(MetadataSet.TWEET_CONTENT));	
		} else {
			txtv.setText("");
		}
		//
		txtv = (TextView)findViewById(R.id.user_profile_txtv_last_tweet_time);
		//
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
		txtv = (TextView)findViewById(R.id.user_profile_txtv_bio);
		txtv.setText(user.getString(MetadataSet.USERACCOUNT_DESCRIPTION));	
		//
		txtv = (TextView)findViewById(R.id.user_profile_txtv_web);
		txtv.setText(user.getString(MetadataSet.USERACCOUNT_URL));	
		//
		txtv = (TextView)findViewById(R.id.user_profile_txtv_tweets);
		txtv.setText(user.getString(MetadataSet.USERACCOUNT_TWEETS_COUNT));	
		//
		txtv = (TextView)findViewById(R.id.user_profile_txtv_friends);
		txtv.setText(user.getString(MetadataSet.USERACCOUNT_FRIENDS_COUNT));	
		//
		txtv = (TextView)findViewById(R.id.user_profile_txtv_followers);
		txtv.setText(user.getString(MetadataSet.USERACCOUNT_FOLLOWERS_COUNT));	
		//
		txtv = (TextView)findViewById(R.id.user_profile_txtv_member_since);
		txtv.setText(
			DateFormat.format(
				"dd/MM/yyyy",
				user.getLong(MetadataSet.USERACCOUNT_CREATE_DATE)));
		//
		ImageView imgV = (ImageView)findViewById(R.id.user_profile_imgv_avatar);
		//
		String imgUrl = null;
		Drawable cachedImage = null;
		//
       	imgUrl = user.getString(MetadataSet.USERACCOUNT_PICTURE_URI);
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
	 * 
	 */
	public void followOrUnfollow() {
		if (isFollowing) {
			new UnfollowAsyncServiceCall(this) {
				@Override
				protected void onPostRun(List<UserAccount> result) {
					isFollowing = false;
				}
			}.execute(user);
		} else {
			new FollowAsyncServiceCall(this) {
				@Override
				protected void onPostRun(List<UserAccount> result) {
					isFollowing = true;
				}
			}.execute(user);
		}
	}
	
	/**
	 * 
	 */
	public void blockOrUnblock() {
		if (isBlocking) {
			new UnblockAsyncServiceCall(this) {
				@Override
				protected void onPostRun(List<UserAccount> result) {
					isBlocking = false;
				}
			}.execute(user);
		} else {
			new BlockAsyncServiceCall(this) {
				@Override
				protected void onPostRun(List<UserAccount> result) {
					isBlocking = true;
				}
			}.execute(user);
		}
	}
	
	/**
	 * 
	 */
	public void reportSpam() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.confirm_report_spam));
		builder.setCancelable(false);
		builder.setPositiveButton(
			getString(R.string.yes), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				new ReportSpamAsyncServiceCall(getParent()).execute(user);
			}
		});
		builder.setNegativeButton(
			getString(R.string.no), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		//
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	/**
	 * 
	 */
	public void editProfile() {
		Intent intent = new Intent(this, EditUserProfileActivity.class);
		intent.putExtra(EditUserProfileActivity.PARAM_KEY_USER, user);
		//
		startActivityForResult(intent, REQUEST_EDIT_USER);
	}
	
	/**
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean result = super.onPrepareOptionsMenu(menu);
		//
		if (!isLoggedUser) {
			menu.findItem(R.id.menu_item_follow).setTitle(
				isFollowing ? R.string.unfollow : R.string.follow);	
			menu.findItem(R.id.menu_item_follow).setIcon(
				isFollowing ? R.drawable.round_minus : R.drawable.round_plus);	
			menu.findItem(R.id.menu_item_block).setTitle(
				isBlocking ? R.string.unblock : R.string.block);
			menu.findItem(R.id.menu_item_block).setIcon(
				isBlocking ? R.drawable.round_checkmark : R.drawable.cancel);	
		}
		//
		return result;
	}
	
	/**
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (isLoggedUser) {
			getMenuInflater().inflate(R.menu.view_my_user_profile, menu);
		} else {
			getMenuInflater().inflate(R.menu.view_user_profile, menu);
		}
		//
		return true;
	}
	
	/**
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.menu_item_follow:
	    	followOrUnfollow();
	    	//
	        return true;
	    case R.id.menu_item_block:
	    	blockOrUnblock();
	    	//
	        return true;
	    case R.id.menu_item_report_spam:
	    	reportSpam();
	    	//
	        return true;
	    case R.id.menu_item_edit_profile:
	    	editProfile();
	    	//
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
}

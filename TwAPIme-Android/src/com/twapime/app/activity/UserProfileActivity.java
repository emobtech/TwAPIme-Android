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
import com.twapime.app.TwAPImeApplication;
import com.twapime.app.service.AddMemberAsyncServiceCall;
import com.twapime.app.service.BlockAsyncServiceCall;
import com.twapime.app.service.FollowAsyncServiceCall;
import com.twapime.app.service.GetFriendshipAsyncServiceCall;
import com.twapime.app.service.GetUserAsyncServiceCall;
import com.twapime.app.service.ReportSpamAsyncServiceCall;
import com.twapime.app.service.UnblockAsyncServiceCall;
import com.twapime.app.service.UnfollowAsyncServiceCall;
import com.twapime.app.util.AsyncImageLoader;
import com.twapime.app.widget.ImageViewCallback;
import com.twitterapime.model.MetadataSet;
import com.twitterapime.rest.Friendship;
import com.twitterapime.rest.UserAccount;

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
	private static final int REQUEST_ADD_TO_LIST = 1;

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
	private boolean isFollowing;
	
	/**
	 * 
	 */
	private boolean isBlocking;
	
	/**
	 * 
	 */
	private boolean isFollowed;

	/**
	 * 
	 */
	private boolean canDM;

	/**
	 * 
	 */
	private boolean isLoggedUser;
	
	/**
	 * 
	 */
	private Friendship friendship;

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		setContentView(R.layout.view_profile);
		//
		TwAPImeApplication app = (TwAPImeApplication)getApplication();
		Intent intent = getIntent();
		//
		user = (UserAccount)intent.getSerializableExtra(PARAM_KEY_USER);
		isLoggedUser = app.isLoggedUser(user);
		//
		loadUserProfile();
		if (!isLoggedUser) {
			loadUserFriendship();
		}
	}
	
	/**
	 * 
	 */
	protected void loadUserProfile() {
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
	protected void loadUserFriendship() {
		new GetFriendshipAsyncServiceCall(this) {
			protected void onPostRun(List<Friendship> result) {
				friendship = result.get(0).getSource();
				//
				isFollowing =
					friendship.getBoolean(MetadataSet.FRIENDSHIP_FOLLOWING);
				isBlocking =
					friendship.getBoolean(MetadataSet.FRIENDSHIP_BLOCKING);
				isFollowed =
					friendship.getBoolean(MetadataSet.FRIENDSHIP_FOLLOWED_BY);
				canDM = friendship.getBoolean(MetadataSet.FRIENDSHIP_CAN_DM);
			};
		}.execute(user);
	}
	
	/**
	 * 
	 */
	protected void displayUserProfile() {
		TextView txtv = (TextView)findViewById(R.id.user_profile_txtv_name);
		txtv.setText(user.getString(MetadataSet.USERACCOUNT_NAME));	
		//
		txtv = (TextView)findViewById(R.id.user_profile_txtv_username);
		txtv.setText("@" + user.getString(MetadataSet.USERACCOUNT_USER_NAME));	
		//
		txtv = (TextView)findViewById(R.id.user_profile_txtv_location);
		txtv.setText(user.getString(MetadataSet.USERACCOUNT_LOCATION));
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
	protected void follow() {
		new FollowAsyncServiceCall(this) {
			@Override
			protected void onPostRun(List<UserAccount> result) {
				isFollowing = true;
			}
		}.execute(user);
	}
	
	/**
	 * 
	 */
	protected void unfollow() {
		new UnfollowAsyncServiceCall(this) {
			@Override
			protected void onPostRun(List<UserAccount> result) {
				isFollowing = false;
			}
		}.execute(user);
	}

	/**
	 * 
	 */
	protected void block() {
		new BlockAsyncServiceCall(this) {
			@Override
			protected void onPostRun(List<UserAccount> result) {
				isBlocking = true;
			}
		}.execute(user);
	}
	
	/**
	 * 
	 */
	protected void unblock() {
		new UnblockAsyncServiceCall(this) {
			@Override
			protected void onPostRun(List<UserAccount> result) {
				isBlocking = false;
			}
		}.execute(user);
	}

	/**
	 * 
	 */
	protected void reportSpam() {
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
	protected void editProfile() {
		Intent intent = new Intent(this, EditUserProfileActivity.class);
		intent.putExtra(EditUserProfileActivity.PARAM_KEY_USER, user);
		//
		startActivityForResult(intent, REQUEST_EDIT_USER);
	}
	
	/**
	 * 
	 */
	protected void addToList() {
		Intent intent = new Intent(this, ListActivity.class);
		intent.setAction(Intent.ACTION_PICK);
		intent.putExtra(ListActivity.PARAM_KEY_USER, user);
		//
		startActivityForResult(intent, REQUEST_ADD_TO_LIST);
	}
	
	/**
	 * @param list
	 */
	protected void addMember(com.twitterapime.rest.List list) {
		new AddMemberAsyncServiceCall(this).execute(list, user);
	}
	
	/**
	 * 
	 */
	protected void newDM() {
		Intent intent = new Intent(this, NewDirectMessageActivity.class);
		intent.putExtra(
			NewDirectMessageActivity.PARAM_KEY_DM_RECIPIENT,
			user.getString(MetadataSet.USERACCOUNT_USER_NAME));
		//
		startActivity(intent);
	}
	
	/**
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
		Intent data) {
		if (resultCode == RESULT_OK && requestCode == REQUEST_ADD_TO_LIST) {
			com.twitterapime.rest.List list =
				(com.twitterapime.rest.List)data.getSerializableExtra(
					ListActivity.RETURN_KEY_PICK_LIST);
			//
			addMember(list);
		}
	}
	
	/**
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (isLoggedUser) {
			getMenuInflater().inflate(R.menu.my_profile, menu);
		} else {
			getMenuInflater().inflate(R.menu.profile, menu);
		}
		//
		return true;
	}

	/**
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean result = super.onPrepareOptionsMenu(menu);
		//
		if (!isLoggedUser) {
			if (friendship == null) {
				menu.removeItem(R.id.menu_item_follow);
				menu.removeItem(R.id.menu_item_unfollow);
				menu.removeItem(R.id.menu_item_unblock);
				menu.removeItem(R.id.menu_item_block);
				menu.removeItem(R.id.menu_item_new_dm);
			} else {
				if (isFollowing) {
					menu.removeItem(R.id.menu_item_follow);
				} else {
					menu.removeItem(R.id.menu_item_unfollow);
				}
				if (isFollowed) {
					if (isBlocking) {
						menu.removeItem(R.id.menu_item_block);
					} else {
						menu.removeItem(R.id.menu_item_unblock);
					}
				} else {
					menu.removeItem(R.id.menu_item_unblock);
					menu.removeItem(R.id.menu_item_block);
				}
				if (!canDM) {
					menu.removeItem(R.id.menu_item_new_dm);
				}
			}
		}
		//
		return result;
	}
	
	/**
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.menu_item_follow:
	    	follow();
	    	//
	        return true;
	    case R.id.menu_item_unfollow:
	    	unfollow();
	    	//
	        return true;
	    case R.id.menu_item_block:
	    	block();
	    	//
	        return true;
	    case R.id.menu_item_unblock:
	    	unblock();
	    	//
	        return true;
	    case R.id.menu_item_report_spam:
	    	reportSpam();
	    	//
	        return true;
	    case R.id.menu_item_edit:
	    	editProfile();
	    	//
	        return true;
	    case R.id.menu_item_add_to_list:
	    	addToList();
	    	//
	        return true;
	    case R.id.menu_item_new_dm:
	    	newDM();
	    	//
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
}

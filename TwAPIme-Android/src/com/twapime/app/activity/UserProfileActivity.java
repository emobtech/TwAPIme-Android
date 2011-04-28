package com.twapime.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.twapime.app.R;
import com.twapime.app.TwAPImeApplication;
import com.twapime.app.util.DateUtil;
import com.twapime.app.util.UIUtil;
import com.twitterapime.model.MetadataSet;
import com.twitterapime.rest.UserAccount;
import com.twitterapime.rest.UserAccountManager;
import com.twitterapime.search.Tweet;

/**
 * @author ernandes@gmail.com
 */
public class UserProfileActivity extends Activity {
	/**
	 * 
	 */
	static final String PARAM_KEY_USER_NAME = "PARAM_KEY_USER_NAME";
	
	/**
	 * 
	 */
	private UserAccount userAccount;

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		setContentView(R.layout.user_profile);
		displayUserProfile();
		//
		Intent intent = getIntent();
		//
		String userID = intent.getExtras().getString(PARAM_KEY_USER_NAME);
		//
		userAccount = new UserAccount(userID);
		//
		loadUserProfile();
	}
	
	/**
	 * 
	 */
	protected void loadUserProfile() {
		final ProgressDialog progressDialog =
			ProgressDialog.show(
				this, "", getString(R.string.loading_user_profile), false);
		//
		new Thread() {
			@Override
			public void run() {
				TwAPImeApplication app = (TwAPImeApplication)getApplication();
				UserAccountManager uam = app.getUserAccountManager();
				//
				try {
					userAccount = uam.getUserAccount(userAccount);
					//
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							displayUserProfile();
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
							UIUtil.showAlertDialog(UserProfileActivity.this, e);
						}
					});
				}
			};
		}.start();
	}
	
	/**
	 * 
	 */
	protected void displayUserProfile() {
		TextView txtv = (TextView)findViewById(R.id.user_profile_txtv_name);
		if (userAccount != null) {
			txtv.setText(userAccount.getString(MetadataSet.USERACCOUNT_NAME));	
		} else {
			txtv.setText("");
		}
		//
		txtv = (TextView)findViewById(R.id.user_profile_txtv_username);
		if (userAccount != null) {
			txtv.setText(
				"@" + userAccount.getString(MetadataSet.USERACCOUNT_USER_NAME));	
		} else {
			txtv.setText("");
		}
		//
		txtv = (TextView)findViewById(R.id.user_profile_txtv_last_tweet);
		if (userAccount != null) {
			Tweet tweet = userAccount.getLastTweet();
			//
			txtv.setText(tweet.getString(MetadataSet.TWEET_CONTENT));	
		} else {
			txtv.setText("");
		}
		//
		txtv = (TextView)findViewById(R.id.user_profile_txtv_last_tweet_time);
		if (userAccount != null) {
			Tweet tweet = userAccount.getLastTweet();
			//
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
		if (userAccount != null) {
			txtv.setText(
				userAccount.getString(MetadataSet.USERACCOUNT_DESCRIPTION));	
		} else {
			txtv.setText("");
		}
		//
		txtv = (TextView)findViewById(R.id.user_profile_txtv_web);
		if (userAccount != null) {
			txtv.setText(userAccount.getString(MetadataSet.USERACCOUNT_URL));	
		} else {
			txtv.setText("");
		}
		//
		txtv = (TextView)findViewById(R.id.user_profile_txtv_tweets);
		if (userAccount != null) {
			txtv.setText(
				userAccount.getString(MetadataSet.USERACCOUNT_TWEETS_COUNT));	
		} else {
			txtv.setText("");
		}
		//
		txtv = (TextView)findViewById(R.id.user_profile_txtv_friends);
		if (userAccount != null) {
			txtv.setText(
				userAccount.getString(MetadataSet.USERACCOUNT_FRIENDS_COUNT));	
		} else {
			txtv.setText("");
		}
		//
		txtv = (TextView)findViewById(R.id.user_profile_txtv_followers);
		if (userAccount != null) {
			txtv.setText(
				userAccount.getString(MetadataSet.USERACCOUNT_FOLLOWERS_COUNT));	
		} else {
			txtv.setText("");
		}
		//
		txtv = (TextView)findViewById(R.id.user_profile_txtv_member_since);
		if (userAccount != null) {
			txtv.setText(
				DateFormat.format("dd/MM/yyyy",
				Long.parseLong(
					userAccount.getString(
						MetadataSet.USERACCOUNT_CREATE_DATE))).toString());
		} else {
			txtv.setText("");
		}
	}
	
	/**
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean result = super.onPrepareOptionsMenu(menu);
		//
//		menu.findItem(R.id.menu_item_favorite).setTitle(
//			isFavoriteTweet() ? R.string.unfavorite : R.string.favorite);	
		//
		return result;
	}
	
	/**
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.view_user_profile, menu);
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
	    	//
	        return true;
	    case R.id.menu_item_block:
	    	//
	        return true;
	    case R.id.menu_item_report_spam:
	    	//
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
}

package com.twapime.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
	static final String PARAM_KEY_USERNAME = "PARAM_KEY_USERNAME";
	
	/**
	 * 
	 */
	private UserAccount userAccount;
	
	/**
	 * 
	 */
	private UserAccountManager userManager;
	
	/**
	 * 
	 */
	private boolean isFollowing;
	
	/**
	 * 
	 */
	private boolean isBlocking;

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
		String userID = intent.getExtras().getString(PARAM_KEY_USERNAME);
		//
		userAccount = new UserAccount(userID);
		//
		TwAPImeApplication app = (TwAPImeApplication)getApplication();
		userManager = app.getUserAccountManager();
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
				try {
					userAccount = userManager.getUserAccount(userAccount);
					isFollowing = userManager.isFollowing(userAccount);
					isBlocking = userManager.isBlocking(userAccount);
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
			if (tweet != null) {
				txtv.setText(tweet.getString(MetadataSet.TWEET_CONTENT));	
			} else {
				txtv.setText("");
			}
		} else {
			txtv.setText("");
		}
		//
		txtv = (TextView)findViewById(R.id.user_profile_txtv_last_tweet_time);
		if (userAccount != null) {
			Tweet tweet = userAccount.getLastTweet();
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
	 * 
	 */
	protected void followOrUnfollow() {
		final ProgressDialog progressDialog =
			ProgressDialog.show(
				this,
				"",
				getString(
					isFollowing ? R.string.unfollowing : R.string.following),
				false);
		//
		new Thread() {
			@Override
			public void run() {
				try {
					if (isFollowing) {
						userAccount = userManager.unfollow(userAccount);
					} else {
						userAccount = userManager.follow(userAccount);
					}
					//
					isFollowing = !isFollowing;
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
	protected void blockOrUnblock() {
		final ProgressDialog progressDialog =
			ProgressDialog.show(
				this,
				"",
				getString(
					isBlocking ? R.string.unblocking : R.string.blocking),
				false);
		//
		new Thread() {
			@Override
			public void run() {
				try {
					if (isFollowing) {
						userAccount = userManager.block(userAccount);
					} else {
						userAccount = userManager.unblock(userAccount);
					}
					//
					isBlocking = !isBlocking;
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
	protected void reportSpam() {
		final ProgressDialog progressDialog =
			ProgressDialog.show(
				this, "", getString(R.string.reporting_spam), false);
		//
		new Thread() {
			@Override
			public void run() {
				try {
					userAccount = userManager.reportSpam(userAccount);
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
							UIUtil.showAlertDialog(UserProfileActivity.this, e);
						}
					});
				}
			};
		}.start();
	}
	
	/**
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean result = super.onPrepareOptionsMenu(menu);
		//
		menu.findItem(R.id.menu_item_follow).setTitle(
			isFollowing ? R.string.unfollow : R.string.follow);	
		menu.findItem(R.id.menu_item_block).setTitle(
			isBlocking ? R.string.unblock : R.string.block);	
		//
		return result;
	}
	
	/**
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		SharedPreferences prefs =
			getSharedPreferences(TwAPImeApplication.PREFS_NAME, MODE_PRIVATE);
		//
		String username =
			prefs.getString(AuthActivity.PREFS_KEY_USERNAME, null);
		//
		if (!username.equalsIgnoreCase(
				userAccount.getString(MetadataSet.USERACCOUNT_USER_NAME))) {
			getMenuInflater().inflate(R.menu.view_user_profile, menu);
		    //
		    return true;
		} else {
			return false;
		}
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
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
}

package com.twapime.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.twapime.app.R;
import com.twapime.app.TwAPImeApplication;
import com.twapime.app.util.DateUtil;
import com.twapime.app.util.UIUtil;
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
		TextView txtv = (TextView)findViewById(R.id.view_tweet_txtv_name);
		txtv.setText("");
		//
		txtv = (TextView)findViewById(R.id.view_tweet_txtv_username);
		txtv.setText("");
		//
		txtv = (TextView)findViewById(R.id.view_tweet_txtv_content);
		txtv.setText("");
		//
		txtv = (TextView)findViewById(R.id.view_tweet_txtv_app);
		txtv.setText("");
		//
		txtv = (TextView)findViewById(R.id.view_tweet_txtv_time);
		txtv.setText("");
		//
		Intent intent = getIntent();
		//
		if (intent.hasExtra(PARAM_KEY_TWEET_ID)) {
			loadTweet(intent.getExtras().getString(PARAM_KEY_TWEET_ID));
		}
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
		UserAccount ua = tweet.getUserAccount();
		//
		TextView txtv = (TextView)findViewById(R.id.view_tweet_txtv_name);
		txtv.setText(ua.getString(MetadataSet.USERACCOUNT_NAME));
		//
		txtv = (TextView)findViewById(R.id.view_tweet_txtv_username);
		txtv.setText("@" + ua.getString(MetadataSet.USERACCOUNT_USER_NAME));
		//
		txtv = (TextView)findViewById(R.id.view_tweet_txtv_content);
		txtv.setText(tweet.getString(MetadataSet.TWEET_CONTENT));
		//
		txtv = (TextView)findViewById(R.id.view_tweet_txtv_app);
		txtv.setText(tweet.getString(MetadataSet.TWEET_SOURCE));
		//
		txtv = (TextView)findViewById(R.id.view_tweet_txtv_time);
		txtv.setText(
			DateUtil.formatTweetDate(
        		Long.parseLong(
            		tweet.getString(MetadataSet.TWEET_PUBLISH_DATE)),
            		this));
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

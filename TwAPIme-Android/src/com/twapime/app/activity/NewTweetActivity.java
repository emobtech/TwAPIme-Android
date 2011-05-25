/*
 * NewTweetActivity.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.activity;

import java.util.Hashtable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.twapime.app.R;
import com.twapime.app.TwAPImeApplication;
import com.twapime.app.util.UIUtil;
import com.twitterapime.model.MetadataSet;
import com.twitterapime.rest.TweetER;
import com.twitterapime.rest.UserAccount;
import com.twitterapime.search.Tweet;

/**
 * @author ernandes@gmail.com
 */
public class NewTweetActivity extends Activity {
	/**
	 * 
	 */
	static final String PARAM_KEY_REPLY_TWEET_ID = "PARAM_KEY_REPLY_TWEET_ID";

	/**
	 * 
	 */
	static final String PARAM_KEY_REPLY_USERNAME = "PARAM_KEY_REPLY_USERNAME";

	/**
	 * 
	 */
	static final String PARAM_KEY_TWEET_CONTENT = "PARAM_KEY_TWEET_CONTENT";
	
	/**
	 * 
	 */
	private TweetER ter;
	
	/**
	 * 
	 */
	private Tweet replyTweet;

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		setContentView(R.layout.new_tweet);
		//
		final Button btnPost = (Button)findViewById(R.id.new_tweet_btn_post);
		btnPost.setEnabled(false);
		btnPost.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				post();
			}
		});
		//
		EditText t = (EditText)findViewById(R.id.new_tweet_txtf_content);
		t.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				TextView tv =
					(TextView)findViewById(R.id.new_tweet_txtv_number_chars);
				//
				tv.setText(s.length() + "");
				btnPost.setEnabled(s.length() > 0);
			}
		});
		//
		Intent intent = getIntent();
		//
		if (intent.hasExtra(PARAM_KEY_TWEET_CONTENT)) {
			String content =
				intent.getExtras().getString(PARAM_KEY_TWEET_CONTENT);
			//
			final int TWEET_LENGTH = 140;
			//
			if (content.trim().length() > TWEET_LENGTH) {
				content = content.substring(0, TWEET_LENGTH - 3) + "...";
			}
			//
			t.setText(content);
			t.setSelection(0);
		}
		//
		if (intent.hasExtra(PARAM_KEY_REPLY_TWEET_ID)
				&& intent.hasExtra(PARAM_KEY_REPLY_USERNAME)) {
			String tweetID =
				intent.getExtras().getString(PARAM_KEY_REPLY_TWEET_ID);
			String username =
				intent.getExtras().getString(PARAM_KEY_REPLY_USERNAME);
			//
			Hashtable<String, Object> data = new Hashtable<String, Object>();
			data.put(MetadataSet.TWEET_ID, tweetID);
			data.put(MetadataSet.TWEET_USER_ACCOUNT, new UserAccount(username));
			//
			replyTweet = new Tweet(data);
		}
		//
		TwAPImeApplication app = (TwAPImeApplication)getApplication();
		ter = TweetER.getInstance(app.getUserAccountManager());
	}
	
	/**
	 * 
	 */
	public void post() {
		final ProgressDialog progressDialog =
			ProgressDialog.show(
				this, "", getString(R.string.posting_tweet), false);
		//
		new Thread() {
			@Override
			public void run() {
				EditText content =
					(EditText)findViewById(R.id.new_tweet_txtf_content);
				Tweet tweet =
					new Tweet(content.getEditableText().toString(), replyTweet);
				//
				try {
					ter.post(tweet);
					//
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							progressDialog.dismiss();
						}
					});
					//
					finish();
				} catch (final Exception e) {
					//
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							progressDialog.dismiss();
							//
							UIUtil.showAlertDialog(NewTweetActivity.this, e);
						}
					});
				}
			}
		}.start();
	}
}

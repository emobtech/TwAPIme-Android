/*
 * NewTweetActivity.java
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
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.twapime.app.R;
import com.twapime.app.service.PostTweetAsyncServiceCall;
import com.twapime.app.widget.SimpleTextWatcher;
import com.twitterapime.model.MetadataSet;
import com.twitterapime.rest.UserAccount;
import com.twitterapime.search.Tweet;

/**
 * @author ernandes@gmail.com
 */
public class NewTweetActivity extends Activity {
	/**
	 * 
	 */
	static final String PARAM_KEY_REPLY_TWEET = "PARAM_KEY_REPLY_TWEET";

	/**
	 * 
	 */
	static final String PARAM_KEY_TWEET_CONTENT = "PARAM_KEY_TWEET_CONTENT";
	
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
	    Button btnCancel = (Button)findViewById(R.id.new_tweet_btn_cancel);
	    btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		//
		final TextView numberOfChars =
			(TextView)findViewById(R.id.new_tweet_txtv_number_chars);
		numberOfChars.setText(Tweet.MAX_CHARACTERS + "");
		//
		EditText t = (EditText)findViewById(R.id.new_tweet_txtf_content);
		t.addTextChangedListener(new SimpleTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				numberOfChars.setText(
					(Tweet.MAX_CHARACTERS - s.length()) + "");
				btnPost.setEnabled(s.length() > 0);
			}
		});
		//
		Intent intent = getIntent();
		//
		if (intent.hasExtra(PARAM_KEY_TWEET_CONTENT)) {
			t.setText(intent.getExtras().getString(PARAM_KEY_TWEET_CONTENT));
			t.setSelection(0);
		}
		//
		if (intent.hasExtra(PARAM_KEY_REPLY_TWEET)) {
			replyTweet =
				(Tweet)intent.getExtras().getSerializable(
					PARAM_KEY_REPLY_TWEET);
			//
			UserAccount user = replyTweet.getUserAccount();
			String usename = user.getString(MetadataSet.USERACCOUNT_USER_NAME);
			//
			t.setText("@" + usename + " ");
			t.setSelection(t.getText().length());
		}
	}
	
	/**
	 * 
	 */
	protected void post() {
		EditText text = (EditText)findViewById(R.id.new_tweet_txtf_content);
		Tweet tweet = new Tweet(text.getEditableText().toString(), replyTweet);
		//
		new PostTweetAsyncServiceCall(this) {
			@Override
			protected void onPostRun(List<Tweet> result) {
				finish();
			};
		}.execute(tweet);
	}
}

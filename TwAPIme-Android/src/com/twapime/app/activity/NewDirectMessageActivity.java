/*
 * NewDirectMessageActivity.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.twapime.app.R;
import com.twapime.app.service.SendTweetAsyncServiceCall;
import com.twapime.app.widget.SimpleTextWatcher;
import com.twitterapime.search.Tweet;

/**
 * @author ernandes@gmail.com
 */
public class NewDirectMessageActivity extends Activity {
	/**
	 * 
	 */
	static final String PARAM_KEY_DM_RECIPIENT = "PARAM_KEY_DM_RECIPIENT";
	
	/**
	 * 
	 */
	private EditText recipient;

	/**
	 * 
	 */
	private EditText content;

	/**
	 * 
	 */
	private GoogleAnalyticsTracker tracker;

	/**
	 * @see com.twapime.app.activity.NewTweetActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		setContentView(R.layout.new_dm);
		//
		final Button btnSend = (Button)findViewById(R.id.new_dm_btn_send);
		btnSend.setEnabled(false);
		btnSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				send();
			}
		});
	    //
	    Button btnCancel = (Button)findViewById(R.id.new_dm_btn_cancel);
	    btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
				//
				tracker.trackEvent("/new_dm", "cancel", null, -1);
			}
		});
		//
		recipient =	(EditText)findViewById(R.id.new_dm_txtf_recipient);
		content = (EditText)findViewById(R.id.new_dm_txtf_content);
		//
		final TextView numberOfChars =
			(TextView)findViewById(R.id.new_dm_txtv_number_chars);
		//
		numberOfChars.setText(Tweet.MAX_CHARACTERS + "");
		//
		SimpleTextWatcher txtWatcher = new SimpleTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (s == content.getEditableText()) {
					numberOfChars.setText(
						(Tweet.MAX_CHARACTERS - s.length()) + "");
				}
				btnSend.setEnabled(
					recipient.length() > 0 && content.length() > 0);
			}
		};
		//
		recipient.addTextChangedListener(txtWatcher);
		content.addTextChangedListener(txtWatcher);
		//
		Intent intent = getIntent();
		//
		if (intent.hasExtra(PARAM_KEY_DM_RECIPIENT)) {
			recipient.setText(
				intent.getExtras().getString(PARAM_KEY_DM_RECIPIENT));
			content.requestFocus();
		} else {
			recipient.requestFocus();
		}
		//
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.trackPageView("/new_dm");
	}
	
	/**
	 * 
	 */
	protected void send() {
		String username = recipient.getText().toString();
		if (username.startsWith("@")) {
			username = username.substring(1);
		}
		//
		Tweet tweet = new Tweet(username, content.getEditableText().toString());
		//
		new SendTweetAsyncServiceCall(this) {
			protected void onPostRun(java.util.List<Tweet> result) {
				finish();
				//
				tracker.trackEvent("/new_dm", "send", null, -1);
			};
		}.execute(tweet);
	}
}

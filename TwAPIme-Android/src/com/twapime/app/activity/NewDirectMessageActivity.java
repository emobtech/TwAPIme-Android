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
import com.twitterapime.rest.TweetER;
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
	private TweetER ter;

	/**
	 * @see com.twapime.app.activity.NewTweetActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		setContentView(R.layout.new_dm);
		//
		final Button btnSend = (Button)findViewById(R.id.new_dm_btn_post);
		btnSend.setEnabled(false);
		btnSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				send();
			}
		});
		//
		final EditText recipient =
			(EditText)findViewById(R.id.new_dm_txtf_recipient);
		final EditText content =
			(EditText)findViewById(R.id.new_dm_txtf_content);
		//
		TextWatcher txtWatcher = new TextWatcher() {
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
				TextView numberOfChars =
					(TextView)findViewById(R.id.new_dm_txtv_number_chars);
				//
				numberOfChars.setText(content.length() + "");
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
		recipient.setText(intent.getExtras().getString(PARAM_KEY_DM_RECIPIENT));
		content.requestFocus();
		//
		TwAPImeApplication app = (TwAPImeApplication)getApplication();
		ter = TweetER.getInstance(app.getUserAccountManager());
	}
	
	/**
	 * 
	 */
	public void send() {
		final ProgressDialog progressDialog =
			ProgressDialog.show(
				this, "", getString(R.string.sending_dm), false);
		//
		new Thread() {
			@Override
			public void run() {
				EditText recipient =
					(EditText)findViewById(R.id.new_dm_txtf_recipient);
				EditText content =
					(EditText)findViewById(R.id.new_dm_txtf_content);
				//
				String username = recipient.getText().toString();
				if (username.startsWith("@")) {
					username = username.substring(1);
				}
				//
				Tweet tweet =
					new Tweet(username, content.getEditableText().toString());
				//
				try {
					ter.send(tweet);
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
							UIUtil.showMessage(
								NewDirectMessageActivity.this, e);
						}
					});
				}
			}
		}.start();
	}
}

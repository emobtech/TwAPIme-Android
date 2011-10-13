/*
 * EditUserProfileActivity.java
 * 02/10/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.activity;

import static com.twitterapime.model.MetadataSet.USERACCOUNT_DESCRIPTION;
import static com.twitterapime.model.MetadataSet.USERACCOUNT_LOCATION;
import static com.twitterapime.model.MetadataSet.USERACCOUNT_NAME;
import static com.twitterapime.model.MetadataSet.USERACCOUNT_URL;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.twapime.app.R;
import com.twapime.app.service.UpdateProfileAsyncServiceCall;
import com.twapime.app.widget.SimpleTextWatcher;
import com.twitterapime.rest.UserAccount;

/**
 * @author ernandes@gmail.com
 */
public class EditUserProfileActivity extends Activity {
	/**
	 * 
	 */
	public static final String PARAM_KEY_USER = "PARAM_KEY_USER";
	
	/**
	 * 
	 */
	public static final String RETURN_KEY_EDIT_USER = "RETURN_KEY_EDIT_USER";

	/**
	 * 
	 */
	private UserAccount user;
	
	/**
	 * 
	 */
	private EditText name;

	/**
	 * 
	 */
	private EditText bio;
	
	/**
	 * 
	 */
	private EditText web;

	/**
	 * 
	 */
	private EditText location;
	
	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		setContentView(R.layout.edit_profile);
	    //
	    final Button btnDone =
	    	(Button)findViewById(R.id.edit_user_profile_btn_done);
	    btnDone.setEnabled(false);
	    btnDone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				done();
			}
		});
	    //
	    Button btnCancel =
	    	(Button)findViewById(R.id.edit_user_profile_btn_cancel);
	    btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
	    //
	    name = (EditText)findViewById(R.id.edit_user_profile_txtf_name);
	    bio = (EditText)findViewById(R.id.edit_user_profile_txtf_bio);
	    web = (EditText)findViewById(R.id.edit_user_profile_txtf_web);
	    location = (EditText)findViewById(R.id.edit_user_profile_txtf_location);
	    //
		name.addTextChangedListener(new SimpleTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				btnDone.setEnabled(s.length() > 0);
			}
		});
		//
		user = (UserAccount)getIntent().getSerializableExtra(PARAM_KEY_USER);
		//
		name.setText(user.getString(USERACCOUNT_NAME));
		bio.setText(user.getString(USERACCOUNT_DESCRIPTION));
		web.setText(user.getString(USERACCOUNT_URL));
		location.setText(user.getString(USERACCOUNT_LOCATION));
	}
	
	/**
	 * 
	 */
	protected void done() {
		user =
			new UserAccount(
				name.getEditableText().toString(),
				bio.getEditableText().toString(),
				web.getEditableText().toString(),
				location.getEditableText().toString());
		//
		new UpdateProfileAsyncServiceCall(this) {
			@Override
			protected void onPostRun(java.util.List<UserAccount> result) {
				Intent intent = new Intent();
			    intent.putExtra(RETURN_KEY_EDIT_USER, result.get(0));
			    //
			    setResult(RESULT_OK, intent);
			    finish();
			}
		}.execute(user);
	}
}

package com.twapime.app.activity;

import com.twapime.app.R;

import android.app.Activity;
import android.os.Bundle;

/**
 * @author ernandes@gmail.com
 */
public class UserProfileActivity extends Activity {
	/**
	 * 
	 */
	static final String PARAM_KEY_USER_ID = "PARAM_KEY_USER_ID";

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		setContentView(R.layout.user_profile);
	}
}

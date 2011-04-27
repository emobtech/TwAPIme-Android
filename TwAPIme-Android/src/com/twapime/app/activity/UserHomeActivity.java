package com.twapime.app.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.twapime.app.R;

/**
 * @author ernandes@gmail.com
 */
public class UserHomeActivity extends TabActivity {
	/**
	 * 
	 */
	static final String PARAM_KEY_USER_ID = "PARAM_KEY_USER_ID";
	
	/**
	 * @see android.app.ActivityGroup#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		setContentView(R.layout.home);
		//
		String userID = null;
		//
		Intent intent = getIntent();
		//
		if (intent.hasExtra(PARAM_KEY_USER_ID)) {
			userID = intent.getExtras().getString(PARAM_KEY_USER_ID);
		}
		//
		TabHost tabHost = getTabHost();
	    TabHost.TabSpec spec;
	    //
	    spec = tabHost.newTabSpec("profile");
	    spec.setIndicator(getString(R.string.profile), null);
	    //
		intent = new Intent(this, UserProfileActivity.class);
		if (userID != null) {
			intent.putExtra(UserProfileActivity.PARAM_KEY_USER_ID, userID);
		}
	    //
	    spec.setContent(intent);
	    tabHost.addTab(spec);
	}
}

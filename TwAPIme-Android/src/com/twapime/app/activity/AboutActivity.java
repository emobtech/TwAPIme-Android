/*
 * AboutActivity.java
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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.twapime.app.R;
import com.twapime.app.service.GetUserAsyncServiceCall;
import com.twitterapime.rest.UserAccount;

/**
 * @author ernandes@gmail.com
 */
public class AboutActivity extends Activity {
	/**
	 * 
	 */
	private GoogleAnalyticsTracker tracker;

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		setContentView(R.layout.about);
		//
		TextView tv = (TextView) findViewById(R.id.about_txtv_description);
		tv.setText(getString(R.string.app_description));
		//
		String version = "...";
		//
		try {
			PackageManager manager = getPackageManager();
			PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
			//
			version = info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		//
		tv = (TextView) findViewById(R.id.about_txtv_version);
		tv.setText(version);
		//
		tv = (TextView) findViewById(R.id.about_txtv_website);
		tv.setText(getString(R.string.app_website));
		//
		tv = (TextView) findViewById(R.id.about_txtv_support);
		tv.setText(Html.fromHtml("<a href='test'>@"
				+ getString(R.string.app_twitter) + "</a>"));
		tv.setClickable(true);
		tv.setFocusable(true);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new GetUserAsyncServiceCall(AboutActivity.this) {
					protected void onPostRun(List<UserAccount> result) {
						Intent intent =
							new Intent(getContext(), UserHomeActivity.class);
						intent.putExtra(
							UserHomeActivity.PARAM_KEY_USER, result.get(0));
						//
						startActivity(intent);
					};
				}.execute(new UserAccount(getString(R.string.app_twitter)));
			}
		});
	    //
		tracker = GoogleAnalyticsTracker.getInstance();
	    tracker.trackPageView("/about");
	}
}

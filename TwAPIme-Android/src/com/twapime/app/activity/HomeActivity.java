/*
 * HomeActivity.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.activity;

import java.util.List;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.twapime.app.R;
import com.twapime.app.TwAPImeApplication;
import com.twapime.app.service.SignOutAsyncServiceCall;
import com.twitterapime.rest.UserAccount;
import com.twitterapime.rest.UserAccountManager;

/**
 * @author ernandes@gmail.com
 */
public class HomeActivity extends TabActivity {
	/**
	 * 
	 */
	private GoogleAnalyticsTracker tracker;
	
	/**
	 * @see android.app.ActivityGroup#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		setContentView(R.layout.home);
		//
		TwAPImeApplication app = (TwAPImeApplication)getApplication();
		//
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession(
			app.getGAAccountId(), app.getGAInterval(), this);
		//
		Resources res = getResources();
		TabHost tabHost = getTabHost();
	    TabHost.TabSpec spec;
	    //
	    spec = tabHost.newTabSpec("home");
	    spec.setIndicator(
	    	getString(R.string.home), res.getDrawable(R.drawable.chat));
	    spec.setContent(new Intent(this, HomeTimelineActivity.class));
	    tabHost.addTab(spec);
	    //
	    spec = tabHost.newTabSpec("mention");
	    spec.setIndicator(
	    	getString(R.string.mentions), res.getDrawable(R.drawable.at));
	    spec.setContent(new Intent(this, MentionTimelineActivity.class));
	    tabHost.addTab(spec);
	    //
	    spec = tabHost.newTabSpec("dm");
	    spec.setIndicator(
	    	getString(
	    		R.string.direct_message), res.getDrawable(R.drawable.mail));
	    spec.setContent(new Intent(this, DirectMessageTimelineActivity.class));
	    tabHost.addTab(spec);
	    //
	    spec = tabHost.newTabSpec("list");
	    spec.setIndicator(
	    	getString(R.string.lists), res.getDrawable(R.drawable.doc_lines));
	    //
	    Intent intent = new Intent(this, ListActivity.class);
	    intent.putExtra(
	    	ListActivity.PARAM_KEY_USER,
	    	new UserAccount(app.getAccessToken().getUsername()));
	    //
	    spec.setContent(intent);
	    tabHost.addTab(spec);
	    //
	    tracker.trackPageView("/home");
	}
	
	/**
	 * @see android.app.ActivityGroup#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//
		tracker.stopSession();
	}
	
	/**
	 * 
	 */
	protected void newTweet() {
		startActivity(new Intent(this, NewTweetActivity.class));
		//
		tracker.trackEvent("/home", "new_tweet", null, -1);
	}
	
	/**
	 * 
	 */
	protected void signOut() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.confirm_sign_out));
		builder.setCancelable(false);
		builder.setPositiveButton(
			getString(R.string.yes), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				final TwAPImeApplication app =
					(TwAPImeApplication)getApplication();
				//
				new SignOutAsyncServiceCall(getParent()) {
					@Override
					protected void onPostRun(List<UserAccountManager> result) {
						app.saveAccessToken(null);
						app.setUserAccountManager(null);
						//
						startActivity(
							new Intent(getParent(), OAuthActivity.class));
						//
						finish();
						//
						tracker.trackEvent("/home", "sign_out", "yes", -1);
					};
				}.execute(app.getUserAccountManager());
			}
		});
		builder.setNegativeButton(
			getString(R.string.no), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				//
				tracker.trackEvent("/home", "sign_out", "no", -1);
			}
		});
		//
		AlertDialog alert = builder.create();
		alert.show();
		//
		tracker.trackEvent("/home", "sign_out", null, -1);
	}
	
	/**
	 * 
	 */
	protected void viewMyProfile() {
		TwAPImeApplication app = (TwAPImeApplication)getApplication();
		//
		Intent intent = new Intent(this, UserHomeActivity.class);
		intent.putExtra(
			UserHomeActivity.PARAM_KEY_USER,
			new UserAccount(app.getAccessToken().getUsername()));
		//
		startActivity(intent);
		//
		tracker.trackEvent("/home", "my_profile", null, -1);
	}
	
	/**
	 * 
	 */
	protected void viewAbout() {
		startActivity(new Intent(this, AboutActivity.class));
		//
		tracker.trackEvent("/home", "about", null, -1);
	}
	
	/**
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home, menu);
	    //
	    return true;
	}
	
	/**
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean result = super.onPrepareOptionsMenu(menu);
		//
		menu.findItem(R.id.menu_item_new_tweet).setTitle(R.string.new_tweet);
		//
		return result;
	}
	
	/**
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.menu_item_new_tweet:
	    	newTweet();
	    	//
	        return true;
	    case R.id.menu_item_my_profile:
	    	viewMyProfile();
	    	//
	        return true;
	    case R.id.menu_item_search:
	    	onSearchRequested();
			//
			tracker.trackEvent("/home", "search", null, -1);
	    	//
	        return true;
	    case R.id.menu_item_about:
	    	viewAbout();
	    	//
	        return true;
	    case R.id.menu_item_sign_out:
	    	signOut();
	    	//
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
}

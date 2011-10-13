/*
 * HomeActivity.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.activity;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

import com.twapime.app.R;
import com.twapime.app.TwAPImeApplication;
import com.twitterapime.rest.UserAccount;

/**
 * @author ernandes@gmail.com
 */
public class HomeActivity extends TabActivity {
	/**
	 * @see android.app.ActivityGroup#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		setContentView(R.layout.home);
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
	    TwAPImeApplication app = (TwAPImeApplication)getApplication();
	    //
	    Intent intent = new Intent(this, ListActivity.class);
	    intent.putExtra(
	    	ListActivity.PARAM_KEY_USER,
	    	new UserAccount(app.getAccessToken().getUsername()));
	    //
	    spec.setContent(intent);
	    tabHost.addTab(spec);
	}
	
	/**
	 * 
	 */
	public void newTweet() {
		startActivity(new Intent(this, NewTweetActivity.class));
	}
	
	/**
	 * 
	 */
	public void signOut() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.confirm_sign_out));
		builder.setCancelable(false);
		builder.setPositiveButton(
			getString(R.string.yes), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				((TwAPImeApplication)getApplication()).saveAccessToken(null);
				//
				startActivity(
					new Intent(HomeActivity.this, OAuthActivity.class));
				//
				finish();
			}
		});
		builder.setNegativeButton(
			getString(R.string.no), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		//
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	/**
	 * 
	 */
	public void viewMyProfile() {
		TwAPImeApplication app = (TwAPImeApplication)getApplication();
		//
		Intent intent = new Intent(this, UserHomeActivity.class);
		intent.putExtra(
			UserHomeActivity.PARAM_KEY_USER,
			new UserAccount(app.getAccessToken().getUsername()));
		//
		startActivity(intent);
	}
	
	/**
	 * 
	 */
	public void viewAbout() {
		startActivity(new Intent(this, AboutActivity.class));
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

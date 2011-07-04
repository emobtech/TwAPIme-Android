/*
 * ListHomeActivity.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

import com.twapime.app.R;

/**
 * @author ernandes@gmail.com
 */
public class ListHomeActivity extends TabActivity {
	/**
	 * 
	 */
	static final String PARAM_KEY_LIST_ID = "PARAM_KEY_LIST_ID";
	
	/**
	 * 
	 */
	static final String PARAM_KEY_LIST_OWNER = "PARAM_KEY_LIST_OWNER";

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
		Intent intent = getIntent();
		//
		String listID = intent.getExtras().getString(PARAM_KEY_LIST_ID);
		String listOwner = intent.getExtras().getString(PARAM_KEY_LIST_OWNER);
		//
		TabHost tabHost = getTabHost();
	    TabHost.TabSpec spec;
	    //
	    spec = tabHost.newTabSpec("timeline");
	    spec.setIndicator(
	    	getString(R.string.tweets), res.getDrawable(R.drawable.chat));
	    //
		intent = new Intent(this, ListTimelineActivity.class);
		intent.putExtra(ListTimelineActivity.PARAM_KEY_LIST_ID, listID);
		intent.putExtra(ListTimelineActivity.PARAM_KEY_LIST_OWNER, listOwner);
	    //
	    spec.setContent(intent);
	    tabHost.addTab(spec);
	    //
	    spec = tabHost.newTabSpec("member");
	    spec.setIndicator(
	    	getString(R.string.members), res.getDrawable(R.drawable.users));
	    //
		intent = new Intent(this, MemberListActivity.class);
		intent.putExtra(MemberListActivity.PARAM_KEY_LIST_ID, listID);
		intent.putExtra(MemberListActivity.PARAM_KEY_LIST_OWNER, listOwner);
	    //
	    spec.setContent(intent);
	    tabHost.addTab(spec);
	}
}

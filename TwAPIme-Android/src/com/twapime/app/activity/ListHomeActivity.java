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
import com.twitterapime.model.MetadataSet;
import com.twitterapime.rest.List;

/**
 * @author ernandes@gmail.com
 */
public class ListHomeActivity extends TabActivity {
	/**
	 * 
	 */
	public static final String PARAM_KEY_LIST = "PARAM_KEY_LIST";

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
		List list = (List)intent.getExtras().getSerializable(PARAM_KEY_LIST);
		//
		TabHost tabHost = getTabHost();
	    TabHost.TabSpec spec;
	    //
	    spec = tabHost.newTabSpec("timeline");
	    spec.setIndicator(
	    	getString(R.string.tweets), res.getDrawable(R.drawable.chat));
	    //
		intent = new Intent(this, ListTimelineActivity.class);
		intent.putExtra(ListTimelineActivity.PARAM_KEY_LIST, list);
	    //
	    spec.setContent(intent);
	    tabHost.addTab(spec);
	    //
	    spec = tabHost.newTabSpec("member");
	    spec.setIndicator(
	    	getString(R.string.members), res.getDrawable(R.drawable.users));
	    //
		intent = new Intent(this, MemberListActivity.class);
		intent.putExtra(MemberListActivity.PARAM_KEY_LIST, list);
	    //
	    spec.setContent(intent);
	    tabHost.addTab(spec);
	    //
	    if ("Public".equalsIgnoreCase(list.getString(MetadataSet.LIST_MODE))) {
		    spec = tabHost.newTabSpec("subscriber");
		    spec.setIndicator(
		    	getString(R.string.subscribers),
		    	res.getDrawable(R.drawable.users));
		    //
			intent = new Intent(this, SubscriberListActivity.class);
			intent.putExtra(SubscriberListActivity.PARAM_KEY_LIST, list);
		    //
		    spec.setContent(intent);
		    tabHost.addTab(spec);
	    }
	}
}

/*
 * SubscriberListActivity.java
 * 05/10/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.activity;

import java.io.IOException;

import android.os.Bundle;

import com.twapime.app.TwAPImeApplication;
import com.twitterapime.model.Cursor;
import com.twitterapime.rest.List;
import com.twitterapime.rest.ListManager;
import com.twitterapime.rest.UserAccountManager;
import com.twitterapime.search.LimitExceededException;

/**
 * @author ernandes@gmail.com
 */
public class SubscriberListActivity extends UserListActivity {
	/**
	 * 
	 */
	public static final String PARAM_KEY_LIST = "PARAM_KEY_LIST";
	
	/**
	 * 
	 */
	private List list;
	
	/**
	 * @see com.twapime.app.activity.TimelineActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		list = (List)getIntent().getExtras().getSerializable(PARAM_KEY_LIST);
		//
		super.onCreate(savedInstanceState);
	}

	/**
	 * @see com.twapime.app.activity.UserListActivity#loadNextPage()
	 */
	@Override
	protected Cursor loadNextPage() throws IOException, LimitExceededException {
		TwAPImeApplication app = (TwAPImeApplication)getApplication();
		UserAccountManager uam = app.getUserAccountManager();
		ListManager lm = ListManager.getInstance(uam);
		//
		if (list != null) {
			return new Cursor(lm.getSubscribers(list), 0, 0);
		} else {
			return new Cursor(new Object[0], 0, 0);
		}
	}
}

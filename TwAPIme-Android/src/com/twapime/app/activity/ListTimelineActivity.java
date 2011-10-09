/*
 * ListTimelineActivity.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.activity;

import android.os.Bundle;

import com.twitterapime.rest.List;
import com.twitterapime.search.Query;
import com.twitterapime.search.QueryComposer;

/**
 * @author ernandes@gmail.com
 */
public class ListTimelineActivity extends TimelineActivity {
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
	 * @see com.twapime.app.activity.TimelineActivity#refresh()
	 */
	@Override
	public void refresh() {
		super.refresh();
		//
		if (list != null) {
			Query query =
				QueryComposer.append(sinceID, QueryComposer.includeRetweets());
			query =
				QueryComposer.append(query, QueryComposer.includeEntities());
			//
			timeline.startGetListTweets(list, query, this);
		}
	}
}

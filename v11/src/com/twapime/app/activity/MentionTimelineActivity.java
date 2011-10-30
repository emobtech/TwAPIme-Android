/*
 * MentionTimelineActivity.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.activity;

import android.os.Bundle;

import com.twitterapime.search.Query;
import com.twitterapime.search.QueryComposer;

/**
 * @author ernandes@gmail.com
 */
public class MentionTimelineActivity extends TimelineActivity {
	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		trackerPage = "/mention_timeline";
		//
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * @see com.twapime.app.activity.TimelineActivity#refresh()
	 */
	@Override
	protected void refresh() {
		super.refresh();
		//
		Query query =
			QueryComposer.append(sinceID, QueryComposer.includeRetweets());
		query = QueryComposer.append(query, QueryComposer.includeEntities());
		//
		timeline.startGetMentions(query, this);
	}
}

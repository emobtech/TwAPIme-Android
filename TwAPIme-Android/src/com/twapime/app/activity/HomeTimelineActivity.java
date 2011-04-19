package com.twapime.app.activity;

import android.os.Bundle;

/**
 * @author ernandes@gmail.com
 */
public class HomeTimelineActivity extends TimelineActivity {
	/**
	 * @see android.app.ActivityGroup#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * @see com.twapime.app.activity.TimelineActivity#refresh()
	 */
	@Override
	public void refresh() {
		super.refresh();
		//
		timeline.startGetHomeTweets(sinceID, this);
	}
}

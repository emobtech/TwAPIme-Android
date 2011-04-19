package com.twapime.app.activity;

import android.os.Bundle;

/**
 * @author ernandes@gmail.com
 */
public class MentionTimelineActivity extends TimelineActivity {
	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
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
		timeline.startGetMentions(sinceID, this);
	}
}

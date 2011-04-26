package com.twapime.app.activity;

import java.util.Hashtable;

import android.content.Intent;
import android.os.Bundle;

import com.twitterapime.model.MetadataSet;
import com.twitterapime.rest.List;
import com.twitterapime.rest.UserAccount;

/**
 * @author ernandes@gmail.com
 */
public class ListTimelineActivity extends TimelineActivity {
	/**
	 * 
	 */
	static final String PARAM_KEY_LIST_ID = "PARAM_KEY_LIST_ID";
	
	/**
	 * 
	 */
	static final String PARAM_KEY_LIST_OWNER = "PARAM_KEY_LIST_OWNER";

	/**
	 * 
	 */
	private List list;

	/**
	 * @see com.twapime.app.activity.TimelineActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		//
		if (intent.hasExtra(PARAM_KEY_LIST_ID)
				&& intent.hasExtra(PARAM_KEY_LIST_OWNER)) {
			String id =	intent.getExtras().getString(PARAM_KEY_LIST_ID);
			String owner = intent.getExtras().getString(PARAM_KEY_LIST_OWNER);
			//
			Hashtable<String, Object> data = new Hashtable<String, Object>();
			data.put(MetadataSet.LIST_ID, id);
			data.put(MetadataSet.LIST_USER_ACCOUNT, new UserAccount(owner));
			//
			list = new List(data);
		}
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
			timeline.startGetListTweets(list, sinceID, this);
		}
	}
}

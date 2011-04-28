package com.twapime.app.activity;

import java.io.IOException;
import java.util.Hashtable;

import android.content.Intent;
import android.os.Bundle;

import com.twapime.app.TwAPImeApplication;
import com.twitterapime.model.Cursor;
import com.twitterapime.model.MetadataSet;
import com.twitterapime.rest.List;
import com.twitterapime.rest.ListManager;
import com.twitterapime.rest.UserAccount;
import com.twitterapime.rest.UserAccountManager;
import com.twitterapime.search.LimitExceededException;

/**
 * @author ernandes@gmail.com
 */
public class MemberListActivity extends UserListActivity {
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
		String id =	intent.getExtras().getString(PARAM_KEY_LIST_ID);
		String owner = intent.getExtras().getString(PARAM_KEY_LIST_OWNER);
		//
		Hashtable<String, Object> data = new Hashtable<String, Object>();
		data.put(MetadataSet.LIST_ID, id);
		data.put(MetadataSet.LIST_USER_ACCOUNT, new UserAccount(owner));
		//
		list = new List(data);
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
			return new Cursor(lm.getMembers(list), 0, 0);
		} else {
			return new Cursor(new Object[0], 0, 0);
		}
	}
}

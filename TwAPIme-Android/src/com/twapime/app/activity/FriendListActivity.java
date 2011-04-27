package com.twapime.app.activity;

import java.io.IOException;

import android.os.Bundle;

import com.twapime.app.TwAPImeApplication;
import com.twitterapime.model.Cursor;
import com.twitterapime.rest.FriendshipManager;
import com.twitterapime.rest.UserAccountManager;
import com.twitterapime.search.LimitExceededException;

/**
 * @author ernandes@gmail.com
 */
public class FriendListActivity extends UserListActivity {
	/**
	 * 
	 */
	protected FriendshipManager friendMngr;
	
	/**
	 * @see com.twapime.app.activity.UserListActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		TwAPImeApplication app = (TwAPImeApplication)getApplication();
		UserAccountManager uam = app.getUserAccountManager();
		//
		friendMngr = FriendshipManager.getInstance(uam);
	}
	
	/**
	 * @see com.twapime.app.activity.UserListActivity#loadNextPage()
	 */
	@Override
	protected Cursor loadNextPage() throws IOException, LimitExceededException {
		return friendMngr.getFriends(nextPageQuery);
	}
}

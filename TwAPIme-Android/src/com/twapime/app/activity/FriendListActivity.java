package com.twapime.app.activity;

import java.io.IOException;

import android.content.Intent;
import android.os.Bundle;

import com.twapime.app.TwAPImeApplication;
import com.twitterapime.model.Cursor;
import com.twitterapime.model.MetadataSet;
import com.twitterapime.rest.FriendshipManager;
import com.twitterapime.rest.UserAccount;
import com.twitterapime.rest.UserAccountManager;
import com.twitterapime.search.LimitExceededException;
import com.twitterapime.search.QueryComposer;

/**
 * @author ernandes@gmail.com
 */
public class FriendListActivity extends UserListActivity {
	/**
	 * 
	 */
	static final String PARAM_KEY_USERNAME = "PARAM_KEY_USERNAME";
	
	/**
	 * 
	 */
	protected UserAccount userAccount;

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
		Intent intent = getIntent();
		//
		String username = intent.getExtras().getString(PARAM_KEY_USERNAME);
		//
		userAccount = new UserAccount(username);
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
		String username =
			userAccount.getString(MetadataSet.USERACCOUNT_USER_NAME);
		//
		if (nextPageQuery != null) {
			nextPageQuery =
				QueryComposer.append(
					nextPageQuery, QueryComposer.screenName(username));
		} else {
			nextPageQuery = QueryComposer.screenName(username);
		}
		//
		return friendMngr.getFriends(nextPageQuery);
	}
}

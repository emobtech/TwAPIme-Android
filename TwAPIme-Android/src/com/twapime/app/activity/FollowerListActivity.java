/*
 * FollowerListActivity.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.activity;

import java.io.IOException;

import com.twitterapime.model.Cursor;
import com.twitterapime.model.MetadataSet;
import com.twitterapime.search.LimitExceededException;
import com.twitterapime.search.QueryComposer;

/**
 * @author ernandes@gmail.com
 */
public class FollowerListActivity extends FriendListActivity {
	/**
	 * @see com.twapime.app.activity.UserListActivity#loadNextPage()
	 */
	@Override
	protected Cursor loadNextPage() throws IOException, LimitExceededException {
		String username = user.getString(MetadataSet.USERACCOUNT_USER_NAME);
		//
		if (nextPageQuery == null) {
			nextPageQuery = QueryComposer.cursor(-1);
		}
		nextPageQuery =
			QueryComposer.append(
				nextPageQuery, QueryComposer.screenName(username));
		//
		return friendMngr.getFollowers(nextPageQuery);
	}
}

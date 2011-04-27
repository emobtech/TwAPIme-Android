package com.twapime.app.activity;

import java.io.IOException;

import com.twitterapime.model.Cursor;
import com.twitterapime.search.LimitExceededException;

/**
 * @author ernandes@gmail.com
 */
public class FollowerListActivity extends FriendListActivity {
	/**
	 * @see com.twapime.app.activity.UserListActivity#loadNextPage()
	 */
	@Override
	protected Cursor loadNextPage() throws IOException, LimitExceededException {
		return friendMngr.getFollowers(nextPageQuery);
	}
}

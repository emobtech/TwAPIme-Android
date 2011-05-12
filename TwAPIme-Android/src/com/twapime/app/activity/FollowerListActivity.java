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
		return friendMngr.getFollowers(nextPageQuery);
	}
}

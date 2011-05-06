package com.twapime.app.activity;

import android.app.SearchManager;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.twapime.app.R;

/**
 * @author ernandes@gmail.com
 */
public class SearchHomeActivity extends TabActivity {
	/**
	 * @see android.app.ActivityGroup#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		setContentView(R.layout.home);
		//
		Intent intent = getIntent();
		String queryStr = null;
		//
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			queryStr = intent.getStringExtra(SearchManager.QUERY);
		}
		//
		TabHost tabHost = getTabHost();
	    TabHost.TabSpec spec;
	    //
	    spec = tabHost.newTabSpec("tweets");
	    spec.setIndicator(getString(R.string.tweets), null);
	    //
		intent = new Intent(this, TweetSearchTimelineActivity.class);
		intent.putExtra(SearchManager.QUERY, queryStr);
	    //
	    spec.setContent(intent);
	    tabHost.addTab(spec);
	    //
	    spec = tabHost.newTabSpec("users");
	    spec.setIndicator(getString(R.string.users), null);
	    //
		intent = new Intent(this, UserSearchListActivity.class);
		intent.putExtra(SearchManager.QUERY, queryStr);
	    //
	    spec.setContent(intent);
	    tabHost.addTab(spec);
	}
}

/*
 * UserListActivity.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.twapime.app.R;
import com.twapime.app.util.UIUtil;
import com.twapime.app.widget.LoadingScrollListener;
import com.twapime.app.widget.UserArrayAdapter;
import com.twitterapime.model.Cursor;
import com.twitterapime.rest.UserAccount;
import com.twitterapime.search.LimitExceededException;
import com.twitterapime.search.Query;
import com.twitterapime.search.QueryComposer;

/**
 * @author ernandes@gmail.com
 */
public abstract class UserListActivity extends ListActivity {
	/**
	 * 
	 */
	public static final String RETURN_KEY_PICK_USER = "RETURN_KEY_PICK_LIST";

	/**
	 * 
	 */
	protected UserArrayAdapter adapter;

	/**
	 * 
	 */
	protected List<UserAccount> users;
	
	/**
	 * 
	 */
	private boolean hasMorePages;
	
	/**
	 * 
	 */
	private Runnable loadNextPageTask;
	
	/**
	 * 
	 */
	protected Query nextPageQuery;
	
	/**
	 * 
	 */
	protected GoogleAnalyticsTracker tracker;
	
	/**
	 * 
	 */
	protected String trackerPage = "/user_list";
	
	/**
	 * 
	 */
	protected boolean pickMode;
	
	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		pickMode = Intent.ACTION_PICK.equals(getIntent().getAction());
		//
		if (pickMode) {
			setTitle(R.string.pick_user);
		}
		//
		users = new ArrayList<UserAccount>();
		adapter = new UserArrayAdapter(this, R.layout.row_user, users);
		setListAdapter(adapter);
		//
		registerForContextMenu(getListView());
		//
		getListView().setOnItemClickListener(
			new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					if (pickMode) {
						picked(users.get(position));
					} else {
						viewUser(users.get(position));
					}
				}
			}
		);
		//
		loadNextPageTask = new Runnable() {
			@Override
			public void run() {
				if (hasMorePages) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							UIUtil.showMessage(
								getApplicationContext(),
								R.string.refreshing_wait);
							//
							tracker.trackEvent(
								trackerPage, "load_next_page", null, -1);
						}
					});
					//
					refresh();
				}
			}
		};
		//
		getListView().setOnScrollListener(
			new LoadingScrollListener(5, loadNextPageTask));
		//
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.trackPageView(trackerPage);
		//
		reload();
	}
	
	/**
	 * 
	 */
	protected void reload() {
		final ProgressDialog progressDialog =
			ProgressDialog.show(
				this, "", getString(R.string.refreshing_wait), false);
		//
		new Thread() {
			public void run() {
				users.clear();
				nextPageQuery = null;
				//
				refresh();
				//
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						progressDialog.dismiss();
					}
				});
			};
		}.start();
	}
	
	/**
	 * 
	 */
	protected void refresh() {
		try {
			Cursor cursor = loadNextPage();
			//
			while (cursor.hasMoreElements()) {
				users.add((UserAccount)cursor.nextElement());
			}
			//
			nextPageQuery = QueryComposer.cursor(cursor.getNextPageIndex());
			hasMorePages = cursor.hasMorePages();
			//
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					adapter.notifyDataSetChanged();
				}
			});
		} catch (final Exception e) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					UIUtil.showMessage(UserListActivity.this, e);
				}
			});
		}
	}
	
	/**
	 * @param user
	 */
	protected void viewUser(UserAccount user) {
		Intent intent = new Intent(this, UserHomeActivity.class);
		intent.putExtra(UserHomeActivity.PARAM_KEY_USER, user);
		//
		startActivity(intent);
		//
		tracker.trackEvent(trackerPage, "view", null, -1);
	}
	
	/**
	 * @param user
	 */
	protected void picked(UserAccount user) {
		Intent intent = new Intent();
	    intent.putExtra(RETURN_KEY_PICK_USER, user);
	    //
		setResult(RESULT_OK, intent);
		finish();
	    //
	    tracker.trackEvent(trackerPage, "pick", null, -1);
	}

	/**
	 * @return
	 */
	protected abstract Cursor loadNextPage() throws IOException, 
		LimitExceededException;
}

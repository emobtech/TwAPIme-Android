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

import com.twapime.app.R;
import com.twapime.app.util.UIUtil;
import com.twapime.app.widget.LoadingScrollListener;
import com.twapime.app.widget.UserArrayAdapter;
import com.twitterapime.model.Cursor;
import com.twitterapime.model.MetadataSet;
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
	private UserArrayAdapter adapter;

	/**
	 * 
	 */
	private List<UserAccount> users;
	
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
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		users = new ArrayList<UserAccount>();
		adapter = new UserArrayAdapter(this, R.layout.user_row, users);
		setListAdapter(adapter);
		//
		registerForContextMenu(getListView());
		//
		getListView().setOnItemClickListener(
			new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					viewUser(position);
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
								getApplicationContext(), R.string.refreshing);
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
		reload();
	}
	
	/**
	 * 
	 */
	protected void reload() {
		final ProgressDialog progressDialog =
			ProgressDialog.show(
				this, "", getString(R.string.refreshing), false);
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
	 * @param index
	 */
	protected void viewUser(int index) {
		UserAccount user = users.get(index);
		//
		Intent intent = new Intent(this, UserHomeActivity.class);
		intent.putExtra(
			UserHomeActivity.PARAM_KEY_USERNAME,
			user.getString(MetadataSet.USERACCOUNT_USER_NAME));
		//
		startActivity(intent);
	}

	/**
	 * @return
	 */
	protected abstract Cursor loadNextPage() throws IOException, 
		LimitExceededException;
}

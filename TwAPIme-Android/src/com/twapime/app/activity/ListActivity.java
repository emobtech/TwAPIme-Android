/*
 * ListActivity.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.twapime.app.R;
import com.twapime.app.TwAPImeApplication;
import com.twapime.app.util.UIUtil;
import com.twapime.app.widget.ListArrayAdapter;
import com.twitterapime.model.MetadataSet;
import com.twitterapime.rest.ListManager;

/**
 * @author ernandes@gmail.com
 */
public class ListActivity extends android.app.ListActivity {
	/**
	 * 
	 */
	private ListArrayAdapter adapter;

	/**
	 * 
	 */
	private List<com.twitterapime.rest.List> lists;

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		lists = new ArrayList<com.twitterapime.rest.List>();
		adapter = new ListArrayAdapter(this, R.layout.list_row, lists);
		setListAdapter(adapter);
		//
		getListView().setOnItemClickListener(
			new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					viewListTweets(position);
				}
			}
		);
		//
		refreshLists();
	}
	
	/**
	 * 
	 */
	protected void refreshLists() {
		final ProgressDialog progressDialog =
			ProgressDialog.show(
				this, "", getString(R.string.refreshing), false);
		//
		new Thread() {
			@Override
			public void run() {
				TwAPImeApplication app = (TwAPImeApplication)getApplication();
				ListManager lmgr =
					ListManager.getInstance(app.getUserAccountManager());
				//
				try {
					com.twitterapime.rest.List[] l = lmgr.getLists();
					//
					lists.clear();
					lists.addAll(Arrays.asList(l));
					//
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							adapter.notifyDataSetChanged();
							//
							progressDialog.dismiss();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							progressDialog.dismiss();
							//
							UIUtil.showAlertDialog(ListActivity.this, e);
						}
					});
				}
			};
		}.start();
	}
	
	/**
	 * @param index
	 */
	protected void viewListTweets(int index) {
		com.twitterapime.rest.List list = lists.get(index);
		//
		Intent intent = new Intent(this, ListHomeActivity.class);
		intent.putExtra(
			ListHomeActivity.PARAM_KEY_LIST_ID,
			list.getString(MetadataSet.LIST_ID));
		intent.putExtra(
			ListHomeActivity.PARAM_KEY_LIST_OWNER,
			list.getUserAccount().getString(MetadataSet.USERACCOUNT_USER_NAME));
		//
		startActivity(intent);
	}
	
	/**
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.menu_item_refresh:
	    	refreshLists();
	    	//
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
}

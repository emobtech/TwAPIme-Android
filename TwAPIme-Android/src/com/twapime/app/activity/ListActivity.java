/*
 * ListActivity.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.activity;

import static com.twapime.app.activity.EditListActivity.PARAM_KEY_LIST;
import static com.twapime.app.activity.EditListActivity.RETURN_KEY_LIST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.twapime.app.R;
import com.twapime.app.service.DeleteListAsyncServiceCall;
import com.twapime.app.service.GetListsAsyncServiceCall;
import com.twapime.app.util.UIUtil;
import com.twapime.app.widget.ListArrayAdapter;
import com.twitterapime.rest.UserAccount;

/**
 * @author ernandes@gmail.com
 */
public class ListActivity extends android.app.ListActivity {
	/**
	 * 
	 */
	private static final int REQUEST_NEW_LIST = 0;

	/**
	 * 
	 */
	private static final int REQUEST_EDIT_LIST = 1;

	/**
	 * 
	 */
	private ListArrayAdapter adapter;

	/**
	 * 
	 */
	private List<com.twitterapime.rest.List> lists;
	
	/**
	 * 
	 */
	private int selectedItemPos;

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
		registerForContextMenu(getListView());
		//
		getListView().setOnItemClickListener(
			new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					viewListTweets(lists.get(position));
				}
			}
		);
		//
		refreshLists();
	}
	
	/**
	 * 
	 */
	public void refreshLists() {
		new GetListsAsyncServiceCall(this) {
			@Override
			protected void onPostRun(com.twitterapime.rest.List[] result) {
				lists.clear();
				lists.addAll(Arrays.asList(result));
				adapter.notifyDataSetChanged();
				//
				if (result.length == 0) {
					UIUtil.showMessage(getContext(), R.string.no_list_found);
				}
			}
		}.execute((UserAccount[])null);
	}
	
	/**
	 * @param index
	 */
	public void viewListTweets(com.twitterapime.rest.List list) {
		Intent intent = new Intent(this, ListHomeActivity.class);
		intent.putExtra(ListHomeActivity.PARAM_KEY_LIST, list);
		//
		startActivity(intent);
	}
	
	/**
	 * 
	 */
	public void newList() {
		startActivityForResult(
			new Intent(this, EditListActivity.class), REQUEST_NEW_LIST);
	}
	
	/**
	 * @param filter
	 */
	public void editList(com.twitterapime.rest.List list) {
		Intent intent = new Intent(this, EditListActivity.class);
		intent.putExtra(PARAM_KEY_LIST, list);
		//
		startActivityForResult(intent, REQUEST_EDIT_LIST);
	}
	
	/**
	 * @param filter
	 */
	public void deleteList(final com.twitterapime.rest.List list) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.confirm_delete_list));
		builder.setCancelable(false);
		builder.setPositiveButton(getString(R.string.yes),
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					new DeleteListAsyncServiceCall(ListActivity.this) {
						@Override
						protected void onPostRun(
							java.util.List<com.twitterapime.rest.List> result) {
							lists.remove(selectedItemPos);
							adapter.notifyDataSetChanged();
						};
						
					}.execute(list);
				}
			});
		builder.setNegativeButton(getString(R.string.no),
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
		//
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	/**
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
		Intent data) {
		if (resultCode == RESULT_OK) {
			if (data != null && data.hasExtra(RETURN_KEY_LIST)) {
				com.twitterapime.rest.List list =
					(com.twitterapime.rest.List)data.getExtras().get(
						RETURN_KEY_LIST);
				//
				if (requestCode == REQUEST_NEW_LIST) {
					lists.add(list);
				} else {
					lists.set(selectedItemPos, list);
				}
				//
				adapter.notifyDataSetChanged();
			}
		}
	}
	
	/**
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean result = super.onPrepareOptionsMenu(menu);
		//
		menu.findItem(R.id.menu_item_new_tweet).setTitle(R.string.new_list);
		//
		return result;
	}
	
	/**
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.menu_item_new_tweet:
	    	newList();
	    	//
	    	return true;
	    case R.id.menu_item_refresh:
	    	refreshLists();
	    	//
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	/**
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, 
		ContextMenuInfo menuInfo) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.edit_list, menu);
	}
	
	/**
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info =
			(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		//
		com.twitterapime.rest.List list = lists.get(info.position);
		//
	    switch (item.getItemId()) {
	    case R.id.menu_item_edit_list:
	    	selectedItemPos = info.position;
	    	editList(list);
	    	//
	        return true;
	    case R.id.menu_item_delete_list:
	    	selectedItemPos = info.position;
	    	deleteList(list);
	    	//
	        return true;
	    default:
	        return super.onContextItemSelected(item);
	    }
	}
}

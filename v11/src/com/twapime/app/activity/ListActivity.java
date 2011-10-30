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
import static com.twapime.app.activity.EditListActivity.RETURN_KEY_EDIT_LIST;
import static com.twitterapime.model.MetadataSet.USERACCOUNT_USER_NAME;

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

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.twapime.app.R;
import com.twapime.app.TwAPImeApplication;
import com.twapime.app.service.DeleteListAsyncServiceCall;
import com.twapime.app.service.GetListsAsyncServiceCall;
import com.twapime.app.service.GetSubscriptionsAsyncServiceCall;
import com.twapime.app.service.SubscribeAsyncServiceCall;
import com.twapime.app.service.UnsubscribeAsyncServiceCall;
import com.twapime.app.widget.ListArrayAdapter;
import com.twitterapime.model.MetadataSet;
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
	public static final String PARAM_KEY_USER = "PARAM_KEY_USER";
	
	/**
	 * 
	 */
	public static final String RETURN_KEY_PICK_LIST = "RETURN_KEY_PICK_LIST";

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
	 * 
	 */
	private UserAccount user;
	
	/**
	 * 
	 */
	private boolean isLoggedUser;
	
	/**
	 * 
	 */
	private boolean pickMode;

	/**
	 * 
	 */
	private GoogleAnalyticsTracker tracker;

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		Intent intent = getIntent();
		//
		lists = new ArrayList<com.twitterapime.rest.List>();
		user = (UserAccount)intent.getSerializableExtra(PARAM_KEY_USER);
		pickMode = Intent.ACTION_PICK.equals(intent.getAction());
		adapter = new ListArrayAdapter(this, R.layout.row_list, lists, user);
		setListAdapter(adapter);
		registerForContextMenu(getListView());
		//
		if (pickMode) {
			setTitle(R.string.pick_list);
		}
		//
		getListView().setOnItemClickListener(
			new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					if (pickMode) {
						picked(lists.get(position));
					} else {
						viewListTweets(lists.get(position));
					}
				}
			}
		);
		//
		refreshLists();
		//
		TwAPImeApplication app = (TwAPImeApplication)getApplicationContext();
		isLoggedUser = app.isLoggedUser(user);
	    //
		tracker = GoogleAnalyticsTracker.getInstance();
		if (pickMode) {
			tracker.trackPageView("/pick_list");
		} else {
			tracker.trackPageView("/list");
		}
	}
	
	/**
	 * 
	 */
	protected void refreshLists() {
		if (pickMode) {
			new GetListsAsyncServiceCall(this) {
				@Override
				protected void onPostRun(com.twitterapime.rest.List[] result) {
					lists.clear();
					lists.addAll(Arrays.asList(result));
					adapter.notifyDataSetChanged();
				}
			}.execute(user);
		} else {
			new GetSubscriptionsAsyncServiceCall(this) {
				@Override
				protected void onPostRun(com.twitterapime.rest.List[] result) {
					lists.clear();
					lists.addAll(Arrays.asList(result));
					adapter.notifyDataSetChanged();
				}
			}.execute(user);
		}
	}
	
	/**
	 * @param list
	 */
	protected void picked(com.twitterapime.rest.List list) {
		Intent intent = new Intent();
	    intent.putExtra(RETURN_KEY_PICK_LIST, list);
	    //
		setResult(RESULT_OK, intent);
		finish();
	    //
	    tracker.trackEvent("/pick_list", "pick", null, -1);
	}
	
	/**
	 * @param index
	 */
	protected void viewListTweets(com.twitterapime.rest.List list) {
		Intent intent = new Intent(this, ListHomeActivity.class);
		intent.putExtra(ListHomeActivity.PARAM_KEY_LIST, list);
		//
		startActivity(intent);
	    //
	    tracker.trackEvent("/list", "view", null, -1);
	}
	
	/**
	 * 
	 */
	protected void newList() {
		startActivityForResult(
			new Intent(this, EditListActivity.class), REQUEST_NEW_LIST);
	    //
	    tracker.trackEvent("/list", "new", null, -1);
	}
	
	/**
	 * @param filter
	 */
	protected void editList(com.twitterapime.rest.List list) {
		Intent intent = new Intent(this, EditListActivity.class);
		intent.putExtra(PARAM_KEY_LIST, list);
		//
		startActivityForResult(intent, REQUEST_EDIT_LIST);
	    //
	    tracker.trackEvent("/list", "edit", null, -1);
	}
	
	/**
	 * @param filter
	 */
	protected void deleteList(final com.twitterapime.rest.List list) {
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
						    //
						    tracker.trackEvent("/list", "delete", "yes", -1);
						};
						
					}.execute(list);
				}
			});
		builder.setNegativeButton(getString(R.string.no),
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				    //
				    tracker.trackEvent("/list", "delete", "no", -1);
				}
			});
		//
		AlertDialog alert = builder.create();
		alert.show();
	    //
	    tracker.trackEvent("/list", "delete", null, -1);
	}
	
	/**
	 * @param list
	 */
	protected void unfollowList(com.twitterapime.rest.List list) {
		new UnsubscribeAsyncServiceCall(this) {
			@Override
			protected void onPostRun(List<com.twitterapime.rest.List> result) {
				if (isLoggedUser) {
					lists.remove(selectedItemPos);
				} else {
					lists.set(selectedItemPos, result.get(0));
				}
				//
				adapter.notifyDataSetChanged();
			    //
			    tracker.trackEvent("/list", "unfollow", null, -1);
			}
		}.execute(list);
	}
	
	/**
	 * @param list
	 */
	protected void followList(com.twitterapime.rest.List list) {
		new SubscribeAsyncServiceCall(this) {
			@Override
			protected void onPostRun(List<com.twitterapime.rest.List> result) {
				lists.set(selectedItemPos, result.get(0));
				adapter.notifyDataSetChanged();
			    //
			    tracker.trackEvent("/list", "follow", null, -1);
			}
		}.execute(list);
	}

	/**
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
		Intent data) {
		if (resultCode == RESULT_OK) {
			if (data != null && data.hasExtra(RETURN_KEY_EDIT_LIST)) {
				com.twitterapime.rest.List list =
					(com.twitterapime.rest.List)data.getExtras().get(
						RETURN_KEY_EDIT_LIST);
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
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (pickMode) {
			return false;
		} else {
			return super.onCreateOptionsMenu(menu);
		}
	}
	
	/**
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean result = super.onPrepareOptionsMenu(menu);
		//
		MenuItem item = menu.findItem(R.id.menu_item_new_tweet);
		if (item != null) {
			item.setTitle(R.string.new_list);
			item.setIcon(R.drawable.follow);
		}
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
		if (pickMode) {
			return;
		}
		//
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.list, menu);
		//
		AdapterView.AdapterContextMenuInfo info =
			(AdapterView.AdapterContextMenuInfo)menuInfo;
		com.twitterapime.rest.List list = lists.get(info.position); 
		UserAccount listOwner = list.getUserAccount();
		//
		if (user.getString(USERACCOUNT_USER_NAME).equalsIgnoreCase(
				listOwner.getString(USERACCOUNT_USER_NAME))	&& isLoggedUser) { //is list owner and logged user?
			menu.removeItem(R.id.menu_item_unfollow);
			menu.removeItem(R.id.menu_item_follow);
		} else {
			menu.removeItem(R.id.menu_item_edit);
			menu.removeItem(R.id.menu_item_delete);
			//
			if (list.getBoolean(MetadataSet.LIST_FOLLOWING)) {
				menu.removeItem(R.id.menu_item_follow);
			} else {
				menu.removeItem(R.id.menu_item_unfollow);
			}
		}
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
	    case R.id.menu_item_edit:
	    	selectedItemPos = info.position;
	    	editList(list);
	    	//
	        return true;
	    case R.id.menu_item_delete:
	    	selectedItemPos = info.position;
	    	deleteList(list);
	    	//
	        return true;
	    case R.id.menu_item_unfollow:
	    	selectedItemPos = info.position;
	    	unfollowList(list);
	    	//
	        return true;
	    case R.id.menu_item_follow:
	    	selectedItemPos = info.position;
	    	followList(list);
	    	//
	        return true;
	    default:
	        return super.onContextItemSelected(item);
	    }
	}
}

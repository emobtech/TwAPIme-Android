/*
 * MemberListActivity.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.activity;

import java.io.IOException;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.twapime.app.R;
import com.twapime.app.TwAPImeApplication;
import com.twapime.app.service.RemoveMemberAsyncServiceCall;
import com.twitterapime.model.Cursor;
import com.twitterapime.rest.List;
import com.twitterapime.rest.ListManager;
import com.twitterapime.rest.UserAccount;
import com.twitterapime.rest.UserAccountManager;
import com.twitterapime.search.LimitExceededException;

/**
 * @author ernandes@gmail.com
 */
public class MemberListActivity extends UserListActivity {
	/**
	 * 
	 */
	public static final String PARAM_KEY_LIST = "PARAM_KEY_LIST";
	
	/**
	 * 
	 */
	private List list;
	
	/**
	 * 
	 */
	private boolean belongsLoggedUser;
	
	/**
	 * @see com.twapime.app.activity.TimelineActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		list = (List)getIntent().getExtras().getSerializable(PARAM_KEY_LIST);
		//
		super.onCreate(savedInstanceState);
		//
		TwAPImeApplication app = (TwAPImeApplication)getApplication();
		belongsLoggedUser = app.isLoggedUser(list.getUserAccount());
	}

	/**
	 * @see com.twapime.app.activity.UserListActivity#loadNextPage()
	 */
	@Override
	protected Cursor loadNextPage() throws IOException, LimitExceededException {
		TwAPImeApplication app = (TwAPImeApplication)getApplication();
		UserAccountManager uam = app.getUserAccountManager();
		ListManager lm = ListManager.getInstance(uam);
		//
		if (list != null) {
			return new Cursor(lm.getMembers(list), 0, 0);
		} else {
			return new Cursor(new Object[0], 0, 0);
		}
	}
	
	/**
	 * @param member
	 */
	protected void removeMember(final UserAccount member) {
		new RemoveMemberAsyncServiceCall(this) {
			@Override
			protected void onPostRun(java.util.List<List> result) {
				users.remove(member);
				adapter.notifyDataSetChanged();
			}
		}.execute(list, member);
	}
	
	/**
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, 
		ContextMenuInfo menuInfo) {
		if (belongsLoggedUser) {
			getMenuInflater().inflate(R.menu.member_list, menu);
		} else {
			super.onCreateContextMenu(menu, v, menuInfo);
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
	    switch (item.getItemId()) {
	    case R.id.menu_item_remove:
	    	removeMember(users.get(info.position));
	    	//
	        return true;
	    default:
	        return super.onContextItemSelected(item);
	    }
	}
}

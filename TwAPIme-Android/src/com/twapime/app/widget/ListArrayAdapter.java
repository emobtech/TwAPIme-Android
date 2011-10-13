/*
 * ListArrayAdapter.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.widget;

import static com.twitterapime.model.MetadataSet.LIST_FOLLOWING;
import static com.twitterapime.model.MetadataSet.LIST_MODE;
import static com.twitterapime.model.MetadataSet.USERACCOUNT_USER_NAME;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.twapime.app.R;
import com.twapime.app.TwAPImeApplication;
import com.twitterapime.model.MetadataSet;
import com.twitterapime.rest.UserAccount;

/**
 * @author ernandes@gmail.com
 */
public class ListArrayAdapter extends ArrayAdapter<com.twitterapime.rest.List> {
	/**
	 * 
	 */
	private Context context;

	/**
	 * 
	 */
	private List<com.twitterapime.rest.List> lists;
	
	/**
	 * 
	 */
	private UserAccount owner;
	
	/**
	 * @param context
	 * @param textViewResourceId
	 * @param lists
	 * @param owner
	 */
	public ListArrayAdapter(Context context, int textViewResourceId,
		List<com.twitterapime.rest.List> lists, UserAccount owner) {
		super(context, textViewResourceId, lists);
		//
		this.context = context;
		this.lists = lists;
		this.owner = owner;
	}
	
	/**
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi =
            	(LayoutInflater)context.getSystemService(
            		Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.row_list, null);
        }
        //
        TwAPImeApplication app =
        	(TwAPImeApplication)context.getApplicationContext();
        com.twitterapime.rest.List list = lists.get(position);
        UserAccount listOwner = list.getUserAccount();
        boolean isListOwner =
        	listOwner.getString(USERACCOUNT_USER_NAME).equalsIgnoreCase(
        		owner.getString(USERACCOUNT_USER_NAME));
        boolean isLoggedUser = app.isLoggedUser(owner);
        //
        String[] privacy =
        	context.getResources().getStringArray(R.array.privacy_array);
        //
        TextView tv = (TextView)v.findViewById(R.id.list_row_txtv_list_privacy);
        //
        if (isListOwner && isLoggedUser) {
        	if ("Public".equalsIgnoreCase(list.getString(LIST_MODE))) {
                tv.setText(privacy[1]);
            } else {
            	tv.setText(privacy[0]);
            }
        } else if (list.getBoolean(LIST_FOLLOWING)) { //is logged user following?
        	tv.setText(R.string.following);
        } else {
        	tv.setText("");
        }
        //
        tv = (TextView) v.findViewById(R.id.list_row_txtv_username);
        //
        if (isListOwner) {
        	tv.setVisibility(View.GONE);
        } else {
        	tv.setVisibility(View.VISIBLE);
           	tv.setText(listOwner.getString(MetadataSet.USERACCOUNT_NAME));	
        }
        //
        tv = (TextView) v.findViewById(R.id.list_row_txtv_list_name);
        tv.setText(list.getString(MetadataSet.LIST_NAME));
        //
		return v;
	}
}

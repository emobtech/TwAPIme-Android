/*
 * ListArrayAdapter.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.widget;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.twapime.app.R;
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
	 * @param context
	 * @param textViewResourceId
	 * @param lists
	 */
	public ListArrayAdapter(Context context, int textViewResourceId,
		List<com.twitterapime.rest.List> lists) {
		super(context, textViewResourceId, lists);
		//
		this.context = context;
		this.lists = lists;
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
            v = vi.inflate(R.layout.list_row, null);
        }
        //
        com.twitterapime.rest.List list = lists.get(position);
        TextView tv = (TextView) v.findViewById(R.id.list_row_txtv_username);
        UserAccount ua = list.getUserAccount();
        //
        if (ua != null) {
        	tv.setText(ua.getString(MetadataSet.USERACCOUNT_NAME));	
        }
        //
        tv = (TextView) v.findViewById(R.id.list_row_txtv_list_name);
        tv.setText(list.getString(MetadataSet.LIST_NAME));
        //
		return v;
	}
}

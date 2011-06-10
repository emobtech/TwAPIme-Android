/*
 * UserArrayAdapter.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.widget;

import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.twapime.app.R;
import com.twapime.app.util.AsyncImageLoader;
import com.twapime.app.util.AsyncImageLoader.ImageLoaderCallback;
import com.twitterapime.model.MetadataSet;
import com.twitterapime.rest.UserAccount;

/**
 * @author ernandes@gmail.com
 */
public class UserArrayAdapter extends ArrayAdapter<UserAccount> {
	/**
	 * 
	 */
	private Context context;

	/**
	 * 
	 */
	private List<UserAccount> users;
	
	/**
	 * 
	 */
	private AsyncImageLoader imageLoader;
	
	/**
	 * 
	 */
	private ImageLoaderCallback callback;
	
	/**
	 * @param context
	 * @param textViewResourceId
	 * @param users
	 */
	public UserArrayAdapter(Context context, int textViewResourceId,
		List<UserAccount> users) {
		super(context, textViewResourceId, users);
		//
		this.context = context;
		this.users = users;
		//
		imageLoader = AsyncImageLoader.getInstance(context);
		callback =
			new ImageViewCallback(
				this, ((ListActivity)context).getListView());
	}
	
	/**
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        //
        if (rowView == null) {
            LayoutInflater vi =
            	(LayoutInflater)context.getSystemService(
            		Context.LAYOUT_INFLATER_SERVICE);
            rowView = vi.inflate(R.layout.user_row, null);
        }
        //
        UserAccount user = users.get(position);
        //
        TextView tv =
        	(TextView)rowView.findViewById(R.id.user_row_txtv_name);
       	tv.setText(user.getString(MetadataSet.USERACCOUNT_NAME));	
        //
        tv = (TextView) rowView.findViewById(R.id.user_row_txtv_username);
        tv.setText("@" + user.getString(MetadataSet.USERACCOUNT_USER_NAME));
        //
        ImageView imgView =
        	(ImageView)rowView.findViewById(R.id.user_row_img_avatar);
        //
        String imageUrl =
        	user.getString(MetadataSet.USERACCOUNT_PICTURE_URI_NORMAL);
        Drawable cachedImage = null;
        //
        if (imageUrl != null) {
            imgView.setTag(imageUrl);
            cachedImage = imageLoader.loadDrawable(imageUrl, callback);
        }
        //
        if (cachedImage == null) {
        	cachedImage = context.getResources().getDrawable(R.drawable.icon);
        }
        //
        imgView.setImageDrawable(cachedImage);
        //
		return rowView;
	}
}

/*
 * TimelineArrayAdapter.java
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
import com.twapime.app.util.DateUtil;
import com.twitterapime.model.MetadataSet;
import com.twitterapime.rest.UserAccount;
import com.twitterapime.search.Tweet;

/**
 * @author ernandes@gmail.com
 */
public class TimelineArrayAdapter extends ArrayAdapter<Tweet> {
	/**
	 * 
	 */
	private Context context;

	/**
	 * 
	 */
	private List<Tweet> tweets;
	
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
	 * @param tweets
	 */
	public TimelineArrayAdapter(final Context context, int textViewResourceId,
		List<Tweet> tweets) {
		super(context, textViewResourceId, tweets);
		//
		this.context = context;
		this.tweets = tweets;
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
            //
            rowView = vi.inflate(R.layout.row_tweet, null);
        }
        //
        Tweet tweet = tweets.get(position);
        UserAccount ua = tweet.getUserAccount();
        String imageUrl = null;
        //
        TextView tv =
        	(TextView)rowView.findViewById(R.id.tweet_row_txtv_username);
        //
       	tv.setText(ua.getString(MetadataSet.USERACCOUNT_NAME));	
       	imageUrl = ua.getString(MetadataSet.USERACCOUNT_PICTURE_URI);
        //
        tv = (TextView)rowView.findViewById(R.id.tweet_row_txtv_content);
        tv.setText(tweet.getString(MetadataSet.TWEET_CONTENT));
        //
        tv = (TextView)rowView.findViewById(R.id.tweet_row_txtv_time);
        tv.setText(
        	DateUtil.formatTweetDate(
        		Long.parseLong(
        			tweet.getString(MetadataSet.TWEET_PUBLISH_DATE)),
        			context));
        //
        ImageView imgView =
        	(ImageView)rowView.findViewById(R.id.tweet_row_img_avatar);
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

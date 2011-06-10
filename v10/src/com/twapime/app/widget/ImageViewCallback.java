/*
 * ImageViewCallback.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.widget;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.twapime.app.util.AsyncImageLoader.ImageLoaderCallback;

/**
 * @author ernandes@gmail.com
 */
public class ImageViewCallback implements ImageLoaderCallback {
	/**
	 * 
	 */
	private BaseAdapter adapter;
	
	/**
	 * 
	 */
	private View parent;
	
	/**
	 * @param adapter
	 */
	public ImageViewCallback(BaseAdapter adapter, View parent) {
		this.adapter = adapter;
		this.parent = parent;
	}

	/**
	 * @see com.twapime.app.util.AsyncImageLoader.ImageLoaderCallback#imageLoaded(android.graphics.drawable.Drawable, java.lang.String)
	 */
	@Override
	public void imageLoaded(Drawable drawable, String url) {
		if (drawable != null) {
	        ImageView imageView = null;
	        //
	        if (parent != null) {
	        	imageView = (ImageView)parent.findViewWithTag(url);
	        }
	        //
	        if (imageView != null) {
	            imageView.setImageDrawable(drawable);
	            //
	            if (adapter != null) {
	                adapter.notifyDataSetChanged();
	            }
	        }
		}
	}
}

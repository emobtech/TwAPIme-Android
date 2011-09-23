/*
 * PostAsyncServiceCall.java
 * 23/09/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.util;

import android.content.Context;

/**
 * @author ernandes@gmail.com
 */
public abstract class PostAsyncServiceCall<P, G, R> 
	extends AsyncServiceCall<P, G, R> {
	/**
	 * @param context
	 */
	public PostAsyncServiceCall(Context context) {
		super(context);
	}
	
	/**
	 * @see com.twapime.app.util.AsyncServiceCall#getProgressStringId()
	 */
	@Override
	public int getProgressStringId() {
		return com.twapime.app.R.string.refreshing;
	}
}

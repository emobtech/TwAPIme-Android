/*
 * GetAsyncServiceCall.java
 * 23/09/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.util;

import android.app.Activity;

/**
 * @author ernandes@gmail.com
 */
public abstract class GetAsyncServiceCall<P, G, R> 
	extends AsyncServiceCall<P, G, R> {
	/**
	 * @param context
	 */
	public GetAsyncServiceCall(Activity context) {
		super(context);
		//
		setProgressStringId(com.twapime.app.R.string.refreshing_wait);
	}
}

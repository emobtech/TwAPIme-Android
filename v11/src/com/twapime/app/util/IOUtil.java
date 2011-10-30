/*
 * IOUtil.java
 * 08/10/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author ernandes@gmail.com
 */
public final class IOUtil {
	/**
	 * @param context
	 * @return
	 */
	public static boolean isOnline(Context context) {
	    ConnectivityManager cm =
	    	(ConnectivityManager)context.getSystemService(
	    		Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    //
	    return netInfo != null && netInfo.isConnectedOrConnecting();
	}
	
	/**
	 * 
	 */
	private IOUtil() {
	}
}

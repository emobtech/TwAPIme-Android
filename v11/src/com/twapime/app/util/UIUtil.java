/*
 * UIUtil.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.util;

import java.io.IOException;

import android.content.Context;
import android.widget.Toast;

import com.twapime.app.R;
import com.twitterapime.search.LimitExceededException;

/**
 * @author ernandes@gmail.com
 */
public final class UIUtil {
	/**
	 * @param context
	 * @param exception
	 */
	public static void showMessage(Context context, Throwable exception) {
		if (exception instanceof IOException) {
			showMessage(context, R.string.network_access_failure);
		} else if (exception instanceof LimitExceededException) {
			showMessage(context, R.string.rate_limit_exceeded);
		} else {
			showMessage(context, exception.getMessage());
		}
	}

	/**
	 * @param context
	 * @param message
	 */
	public static void showMessage(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * @param context
	 * @param resId
	 */
	public static void showMessage(Context context, int resId) {
		showMessage(context, context.getResources().getString(resId));
	}
	
	/**
	 * @param exception
	 * @return
	 */
	public static int getMessageId(Throwable exception) {
		if (exception instanceof IOException) {
			return R.string.network_access_failure;
		} else if (exception instanceof LimitExceededException) {
			return R.string.rate_limit_exceeded;
		} else {
			return R.string.unknown_failure;
		}
	}

	/**
	 * 
	 */
	private UIUtil() {
	}
}

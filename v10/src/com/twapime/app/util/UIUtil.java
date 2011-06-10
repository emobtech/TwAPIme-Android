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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

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
	public static void showAlertDialog(Context context, Throwable exception) {
		if (exception instanceof IOException) {
			showAlertDialog(
				context,
				context.getString(R.string.network_access_failure));
		} else if (exception instanceof LimitExceededException) {
			showAlertDialog(
				context, context.getString(R.string.rate_limit_exceeded));
		} else {
			showAlertDialog(context, exception.getMessage());
		}
	}

	/**
	 * @param context
	 * @param message
	 */
	public static void showAlertDialog(Context context, String message) {
		AlertDialog alert = new AlertDialog.Builder(context).create();
		alert.setTitle(context.getString(R.string.app_name));
		alert.setMessage(message);
		alert.setButton(
			context.getString(R.string.ok),
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {}
			}
		);
		alert.setIcon(R.drawable.icon);
		alert.show();
	}
	
	/**
	 * 
	 */
	private UIUtil() {
	}
}

package com.twapime.app.util;

import android.content.Context;
import android.text.format.DateFormat;

import com.twapime.app.R;

/**
 * @author ernandes@gmail.com
 */
public final class DateUtil {
	/**
	 * @param time
	 * @param context
	 * @return
	 */
	public static String formatTweetDate(long time, Context context) {
		final long OS = 1000;
		final long OM = OS * 60;
		final long OH = OM * 60;
		final long T4H = OH * 24;
		//
		long ellapsed = System.currentTimeMillis() - time;
		//
		if (ellapsed < OM) {
			ellapsed = ellapsed / OS;
			if (ellapsed == 1) {
				return ellapsed + " " + context.getString(R.string.second);
			} else {
				return ellapsed + " " + context.getString(R.string.seconds);
			}
		} else if (ellapsed < OH) {
			ellapsed = ellapsed / OM;
			if (ellapsed == 1) {
				return ellapsed + " " + context.getString(R.string.minute);
			} else {
				return ellapsed + " " + context.getString(R.string.minutes);
			}
		} else if (ellapsed < T4H) {
			ellapsed = ellapsed / OH;
			if (ellapsed == 1) {
				return ellapsed + " " + context.getString(R.string.hour);
			} else {
				return ellapsed + " " + context.getString(R.string.hours);
			}
		} else {
			return DateFormat.format("dd/MM", time).toString();	
		}
	}

	/**
	 * 
	 */
	private DateUtil() {
	}
}

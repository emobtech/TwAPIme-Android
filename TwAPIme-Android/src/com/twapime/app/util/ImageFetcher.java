package com.twapime.app.util;

import java.util.HashMap;
import java.util.Map;

import android.widget.ImageView;

/**
 * @author ernandes@gmail.com
 */
public final class ImageFetcher {
	/**
	 * 
	 */
	private static final Map<String, BitmapFetcherTask> imagePool =
		new HashMap<String, BitmapFetcherTask>();
	
	/**
	 * @param url
	 * @param imageView
	 */
	public static synchronized void fetch(String url, ImageView imageView) {
		BitmapFetcherTask task = imagePool.get(url);
		//
		if (task != null) {
			if (task.getBitmap() != null) {
				imageView.setImageBitmap(task.getBitmap());
			} else {
				task.addHolder(imageView);
			}
		} else {
			task = new BitmapFetcherTask(imageView);
	        task.execute(url);
	        //
			imagePool.put(url, task);
		}
    }
	
	/**
	 * 
	 */
	private ImageFetcher() {
	}
}

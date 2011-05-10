package com.twapime.app.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

/**
 * @author ernandes@gmail.com
 */
class BitmapFetcherTask extends AsyncTask<String, Void, Bitmap> {
	/**
	 * 
	 */
	private List<WeakReference<ImageView>> imageViewReferences;
	
	/**
	 * 
	 */
	private Bitmap bitmap;

	/**
	 * @param imageView
	 */
	public BitmapFetcherTask(ImageView imageView) {
		imageViewReferences = new ArrayList<WeakReference<ImageView>>();
		//
		addHolder(imageView);
	}
	
	/**
	 * @param imageView
	 */
	public synchronized void addHolder(ImageView imageView) {
		for (WeakReference<ImageView> ref : imageViewReferences) {
			if (ref.get() == imageView) {
				return;
			}
		}
		//
		imageViewReferences.add(new WeakReference<ImageView>(imageView));
	}
	
	/**
	 * @return
	 */
	public Bitmap getBitmap() {
		return bitmap;
	}

	/**
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Bitmap doInBackground(String... params) {
		Bitmap bitmap = openBitmapFromFile(params[0]);
		//
		if (bitmap == null) {
			bitmap = downloadBitmapUrl(params[0]);
		}
		//
		return bitmap;
	}
	
	/**
	 * @param url
	 * @return
	 */
	protected Bitmap openBitmapFromFile(String url) {
		return null;
	}
	
	/**
	 * @param bitmap
	 */
	protected void saveBitmapToFile(Bitmap bitmap) {
	}
	
	/**
	 * @param url
	 * @return
	 */
	protected Bitmap downloadBitmapUrl(String url) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        //
		try {
			HttpResponse response = httpClient.execute(request);
			InputStream stream = response.getEntity().getContent();
			//
			Log.d("twapime", "Downloaded: " + url);
			//
			return BitmapFactory.decodeStream(stream);
		} catch (IOException e) {
			Log.d("twapime", e.getMessage());
		}
		//
		return null;
	}

	/**
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected synchronized void onPostExecute(Bitmap bitmap) {
		if (isCancelled()) {
			bitmap = null;
		}
		//
		this.bitmap = bitmap;
		//
		for (WeakReference<ImageView> ref : imageViewReferences) {
			ImageView imageView = ref.get();
			//
			if (imageView != null) {
				imageView.setImageBitmap(bitmap);
			}
		}
		//
		imageViewReferences.clear();
	}
}

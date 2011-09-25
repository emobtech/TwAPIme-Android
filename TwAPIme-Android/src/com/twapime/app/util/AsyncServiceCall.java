/*
 * AsyncServiceCall.java
 * 23/09/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.util;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.twitterapime.search.LimitExceededException;

/**
 * @author ernandes@gmail.com
 */
public abstract class AsyncServiceCall<P, G, R> 
	extends AsyncTask<P, G, Throwable> {
	/**
	 * 
	 */
	private Activity context;
	
	/**
	 * 
	 */
	private int progressStringId;
	
	/**
	 * 
	 */
	private int successStringId;
	
	/**
	 * 
	 */
	private int failureStringId;
	
	/**
	 * 
	 */
	private ProgressDialog progressDialog;
	
	/**
	 * 
	 */
	private R resultRun;
	
	/**
	 * @param context
	 */
	public AsyncServiceCall(Activity context) {
		if (context == null) {
			throw new IllegalArgumentException("Context must not be null.");
		}
		//
		this.context = context;
		progressStringId = -1;
		successStringId = -1;
		failureStringId = -1;
	}
	
	/**
	 * @return
	 */
	public int getProgressStringId() {
		return progressStringId;
	}

	/**
	 * @param strId
	 */
	public void setProgressStringId(int strId) {
		this.progressStringId = strId;
	}

	/**
	 * @return
	 */
	public int getSuccessStringId() {
		return successStringId;
	}

	/**
	 * @param strId
	 */
	public void setSuccessStringId(int strId) {
		this.successStringId = strId;
	}

	/**
	 * @return
	 */
	public int getFailureStringId() {
		return failureStringId;
	}

	/**
	 * @param strId
	 */
	public void setFailureStringId(int strId) {
		this.failureStringId = strId;
	}
	
	/**
	 * @return
	 */
	protected Activity getContext() {
		return context;
	}

	/**
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected final void onPreExecute() {
		if (getProgressStringId() != -1) {
			progressDialog =
				ProgressDialog.show(
					context,
					"",
					context.getString(getProgressStringId()),
					false);
		}
		//
		onPreRun();
	}

	/**
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected final Throwable doInBackground(P... params) {
		try {
			resultRun = run(params);
		} catch (Throwable e) {
			return e;
		}
		//
		return null;
	}
	
	/**
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	protected final void onPostExecute(Throwable result) {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
		//
		if (result != null) {
			if (getFailureStringId() != -1) {
				UIUtil.showMessage(context, getFailureStringId());
			} else {
				UIUtil.showMessage(context, result);
			}
			//
			onFailedRun(result);
		} else {
			if (getSuccessStringId() != -1) {
				UIUtil.showMessage(context, getSuccessStringId());
			}
			//
			onPostRun(resultRun);
		}
	};
	
	/**
	 * 
	 */
	protected void onPreRun() {}
	
	/**
	 * @param params
	 * @return
	 * @throws IOException
	 * @throws LimitExceededException
	 */
	protected abstract R run(P... params) throws IOException,
		LimitExceededException;
	
	/**
	 * @param result
	 */
	protected void onPostRun(R result) {}
	
	/**
	 * @param result
	 */
	protected void onFailedRun(Throwable result) {}
}

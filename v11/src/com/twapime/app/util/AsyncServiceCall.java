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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
	 * 
	 */
	private boolean retry;
	
	/**
	 * 
	 */
	private boolean retryEnabled;
	
	/**
	 * @param ctx
	 */
	public AsyncServiceCall(Activity ctx) {
		if (ctx == null) {
			throw new IllegalArgumentException("Context must not be null.");
		}
		//
		context = ctx;
		progressStringId = -1;
		successStringId = -1;
		failureStringId = -1;
		retryEnabled = true;
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
	 * @param enabled
	 */
	public void setRetry(boolean enabled) {
		retryEnabled = enabled;
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
		Throwable error = null;
		//
		do {
			try {
				if (!IOUtil.isOnline(context)) {
					return new IOException();
				}
				//
				resultRun = run(params);
				//
				return null;
			} catch (Throwable e) {
				if (retryEnabled) {
					retry(e);
				}
				error = e;
			}
		} while (retry);
		//
		return error;
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
			if (!retryEnabled) {
				if (getFailureStringId() != -1) {
					UIUtil.showMessage(context, getFailureStringId());
				} else {
					UIUtil.showMessage(context, result);
				}
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

	/**
	 * @param exception
	 */
	private void retry(Throwable exception) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		//
		builder.setTitle(context.getString(com.twapime.app.R.string.app_name));
		builder.setMessage(getRetryMessage(exception));
		builder.setCancelable(false);
		builder.setPositiveButton(
			context.getString(com.twapime.app.R.string.yes),
			new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				retry = true;
				//
				synchronized (AsyncServiceCall.this) {
					AsyncServiceCall.this.notify();
				}
			}
		});
		builder.setNegativeButton(
			context.getString(com.twapime.app.R.string.no),
			new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				retry = false;
				//
				synchronized (AsyncServiceCall.this) {
					AsyncServiceCall.this.notify();
				}
			}
		});
		//
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				builder.create().show();
			}
		});
		//
		synchronized (AsyncServiceCall.this) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @param exception
	 * @return
	 */
	private String getRetryMessage(Throwable exception) {
		String retryMsg = context.getString(com.twapime.app.R.string.try_again);
		//
		if (getFailureStringId() != -1) {
			retryMsg =
				context.getString(getFailureStringId()) + " " + retryMsg;
		} else {
			retryMsg =
				context.getString(
					UIUtil.getMessageId(exception)) + " " +retryMsg;
		}
		//
		return retryMsg;
	}
}

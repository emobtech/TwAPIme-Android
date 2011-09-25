/*
 * UpdateListAsyncServiceCall.java
 * 23/09/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.service;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;

import com.twapime.app.TwAPImeApplication;
import com.twapime.app.util.PostAsyncServiceCall;
import com.twitterapime.rest.List;
import com.twitterapime.rest.ListManager;
import com.twitterapime.search.LimitExceededException;

/**
 * @author ernandes@gmail.com
 */
public class UpdateListAsyncServiceCall 
	extends PostAsyncServiceCall<List, Void, java.util.List<List>> {
	/**
	 * @param context
	 */
	public UpdateListAsyncServiceCall(Activity context) {
		super(context);
	}

	/**
	 * @see com.twapime.app.util.AsyncServiceCall#run(P[])
	 */
	@Override
	protected java.util.List<List> run(List... params) throws IOException,
		LimitExceededException {
		TwAPImeApplication app = 
			(TwAPImeApplication)getContext().getApplicationContext();
		ListManager lmgr = app.getListManager();
		java.util.List<List> result = new ArrayList<List>();
		//
		for (List list : params) {
			result.add(lmgr.update(list));
		}
		//
		return result;
	}
}

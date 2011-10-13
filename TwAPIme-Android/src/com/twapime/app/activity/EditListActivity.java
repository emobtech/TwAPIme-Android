/*
 * EditListActivity.java
 * 18/09/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.activity;

import static com.twitterapime.model.MetadataSet.LIST_DESCRIPTION;
import static com.twitterapime.model.MetadataSet.LIST_ID;
import static com.twitterapime.model.MetadataSet.LIST_MODE;
import static com.twitterapime.model.MetadataSet.LIST_NAME;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.twapime.app.R;
import com.twapime.app.service.CreateListAsyncServiceCall;
import com.twapime.app.service.UpdateListAsyncServiceCall;
import com.twapime.app.widget.SimpleTextWatcher;
import com.twitterapime.rest.List;

/**
 * @author ernandes@gmail.com
 */
public class EditListActivity extends Activity {
	/**
	 * 
	 */
	public static final String PARAM_KEY_LIST = "PARAM_KEY_LIST";
	
	/**
	 * 
	 */
	public static final String RETURN_KEY_EDIT_LIST = "RETURN_KEY_EDIT_LIST";

	/**
	 * 
	 */
	private List list;
	
	/**
	 * 
	 */
	private EditText name;

	/**
	 * 
	 */
	private EditText description;
	
	/**
	 * 
	 */
	private Spinner privacy;
	
	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		setContentView(R.layout.edit_list);
		//
		privacy = (Spinner)findViewById(R.id.edit_list_spnr_privacy);
	    ArrayAdapter<CharSequence> adapter =
	    	ArrayAdapter.createFromResource(
	    		this,
	    		R.array.privacy_array,
	    		android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(
	    	android.R.layout.simple_spinner_dropdown_item);
	    privacy.setAdapter(adapter);
	    //
	    final Button btnDone = (Button)findViewById(R.id.edit_list_btn_done);
	    btnDone.setEnabled(false);
	    btnDone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				done();
			}
		});
	    //
	    Button btnCancel = (Button)findViewById(R.id.edit_list_btn_cancel);
	    btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
	    //
	    name = (EditText)findViewById(R.id.edit_list_txtf_name);
	    description = (EditText)findViewById(R.id.edit_list_txtf_description);
	    //
		name.addTextChangedListener(new SimpleTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				btnDone.setEnabled(s.length() > 0);
			}
		});
		//
		Intent intent = getIntent();
		if (intent.hasExtra(PARAM_KEY_LIST)) {
			list = (List)intent.getExtras().get(PARAM_KEY_LIST);
			//
			name.setText(list.getString(LIST_NAME));
			privacy.setSelection(
				"private".equals(list.getString(LIST_MODE)) ? 0 : 1);
			description.setText(list.getString(LIST_DESCRIPTION));
		}
	}
	
	/**
	 * 
	 */
	protected void done() {
		if (list == null) {
			List newList =
				new List(
					name.getEditableText().toString(),
					privacy.getSelectedItemPosition() == 1, //is private?
					description.getEditableText().toString());
			//
			new CreateListAsyncServiceCall(this) {
				@Override
				protected void onPostRun(java.util.List<List> result) {
					Intent intent = new Intent();
				    intent.putExtra(RETURN_KEY_EDIT_LIST, result.get(0));
				    //
				    setResult(RESULT_OK, intent);
				    finish();
				}
			}.execute(newList);
		} else {
			List editList =
				new List(
					list.getString(LIST_ID),
					name.getEditableText().toString(),
					privacy.getSelectedItemPosition() == 1, //is private?
					description.getEditableText().toString());
			//
			new UpdateListAsyncServiceCall(this) {
				@Override
				protected void onPostRun(java.util.List<List> result) {
					Intent intent = new Intent();
				    intent.putExtra(RETURN_KEY_EDIT_LIST, result.get(0));
				    //
				    setResult(RESULT_OK, intent);
				    finish();
				}
			}.execute(editList);
		}
	}
}

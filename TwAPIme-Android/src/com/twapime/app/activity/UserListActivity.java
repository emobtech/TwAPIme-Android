package com.twapime.app.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.twapime.app.R;
import com.twapime.app.util.UIUtil;
import com.twapime.app.widget.UserArrayAdapter;
import com.twitterapime.model.Cursor;
import com.twitterapime.model.MetadataSet;
import com.twitterapime.rest.UserAccount;
import com.twitterapime.search.LimitExceededException;

/**
 * @author ernandes@gmail.com
 */
public class UserListActivity extends ListActivity {
	/**
	 * 
	 */
	private UserArrayAdapter adapter;

	/**
	 * 
	 */
	private List<UserAccount> users;
	
	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		users = new ArrayList<UserAccount>();
		adapter = new UserArrayAdapter(this, R.layout.user_row, users);
		setListAdapter(adapter);
		//
		registerForContextMenu(getListView());
		//
		getListView().setOnItemClickListener(
			new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					viewUser(position);
				}
			}
		);
		//
		refresh();
	}
	
	/**
	 * 
	 */
	public void refresh() {
		final ProgressDialog progressDialog =
			ProgressDialog.show(
				this, "", getString(R.string.refreshing), false);
		//
		new Thread() {
			@Override
			public void run() {
				try {
					Cursor cursor = loadUsers();
					//
					while (cursor.hasMoreElements()) {
						users.add((UserAccount)cursor.nextElement());
					}
					//
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							adapter.notifyDataSetChanged();
							//
							progressDialog.dismiss();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							progressDialog.dismiss();
							//
							UIUtil.showAlertDialog(UserListActivity.this, e);
						}
					});
				}
			};
		}.start();
	}
	
	/**
	 * @param index
	 */
	public void viewUser(int index) {
//		Tweet tweet = users.get(index);
//		//
//		Intent intent = new Intent(this, ViewTweetActivity.class);
//		intent.putExtra(
//			ViewTweetActivity.PARAM_KEY_TWEET_ID,
//			tweet.getString(MetadataSet.TWEET_ID));
//		//
//		startActivity(intent);
	}

	/**
	 * 
	 */
	public void sort() {
		Collections.sort(users, new Comparator<UserAccount>() {
			@Override
			public int compare(UserAccount u1, UserAccount u2) {
				String un1 = u1.getString(MetadataSet.USERACCOUNT_NAME);
				String un2 = u2.getString(MetadataSet.USERACCOUNT_NAME);
				//
				return un1.compareTo(un2);
			}
		});
	}

	/**
	 * @return
	 */
	protected Cursor loadUsers() throws IOException, LimitExceededException {
		return new Cursor(new Object[0], 0, 0);
	}
}

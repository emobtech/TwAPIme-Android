package com.twapime.app.activity;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

import com.twapime.app.R;
import com.twapime.app.TwAPImeApplication;

/**
 * @author ernandes@gmail.com
 */
public class HomeActivity extends TabActivity {
	/**
	 * @see android.app.ActivityGroup#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		setContentView(R.layout.home);
		//
		TabHost tabHost = getTabHost();
	    TabHost.TabSpec spec;

	    spec = tabHost.newTabSpec("home");
	    spec.setIndicator(getString(R.string.home), null);
	    spec.setContent(new Intent(this, HomeTimelineActivity.class));
	    tabHost.addTab(spec);
	    
	    spec = tabHost.newTabSpec("mention");
	    spec.setIndicator(getString(R.string.mentions), null);
	    spec.setContent(new Intent(this, MentionTimelineActivity.class));
	    tabHost.addTab(spec);

	    spec = tabHost.newTabSpec("dm");
	    spec.setIndicator(getString(R.string.direct_message), null);
	    spec.setContent(new Intent(this, DirectMessageTimelineActivity.class));
	    tabHost.addTab(spec);
	    
	    spec = tabHost.newTabSpec("list");
	    spec.setIndicator(getString(R.string.lists), null);
	    spec.setContent(new Intent(this, ListActivity.class));
	    tabHost.addTab(spec);
	}
	
	/**
	 * 
	 */
	public void newTweet() {
		startActivity(new Intent(this, NewTweetActivity.class));
	}
	
	/**
	 * 
	 */
	public void signOut() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.confirm_sign_out));
		builder.setCancelable(false);
		builder.setPositiveButton(
			getString(R.string.yes), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				removeCredentials();
				//
				startActivity(
					new Intent(HomeActivity.this, AuthActivity.class));
				//
				finish();
			}
		});
		builder.setNegativeButton(
			getString(R.string.no), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		//
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	/**
	 * 
	 */
	public void removeCredentials() {
		SharedPreferences.Editor editor = getSharedPreferences(
			TwAPImeApplication.PREFS_NAME, MODE_PRIVATE).edit();
		//
		editor.remove(AuthActivity.PREFS_KEY_USERNAME);
		editor.remove(AuthActivity.PREFS_KEY_TOKEN);
		editor.remove(AuthActivity.PREFS_KEY_TOKEN_SECRET);
		//
		editor.commit();
	}
	
	/**
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.timeline, menu);
	    //
	    return true;
	}
	
	/**
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean result = super.onPrepareOptionsMenu(menu);
		//
		menu.findItem(R.id.menu_item_new_tweet).setTitle(R.string.new_tweet);
		//
		return result;
	}
	
	/**
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.menu_item_new_tweet:
	    	newTweet();
	    	//
	        return true;
	    case R.id.menu_item_my_profile:
	        return true;
	    case R.id.menu_item_about:
	        return true;
	    case R.id.menu_item_sign_out:
	    	signOut();
	    	//
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
}

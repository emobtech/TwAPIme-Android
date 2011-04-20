package com.twapime.app.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twapime.app.R;
import com.twapime.app.util.UIUtil;
import com.twitterapime.search.Tweet;

/**
 * @author ernandes@gmail.com
 */
public class NewDirectMessageActivity extends NewTweetActivity {
	/**
	 * 
	 */
	static final String PARAM_KEY_DM_RECIPIENT = "PARAM_KEY_DM_RECIPIENT";
	
	/**
	 * 
	 */
	private EditText recipient;

	/**
	 * @see com.twapime.app.activity.NewTweetActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		LinearLayout lnl = (LinearLayout)findViewById(R.id.new_tweet);
		//
		recipient = new EditText(this);
		lnl.addView(recipient, 0);
		//
		TextView label = new TextView(this);
		label.setText(getString(R.string.recipient));
		lnl.addView(label, 0);
		//
		Button btnSend = (Button)findViewById(R.id.new_tweet_btn_post);
		btnSend.setText(getString(R.string.send));
		//
		Intent intent = getIntent();
		//
		if (intent.hasExtra(PARAM_KEY_DM_RECIPIENT)) {
			recipient.setText(
				intent.getExtras().getString(PARAM_KEY_DM_RECIPIENT));
			//
			EditText content =
				(EditText)findViewById(R.id.new_tweet_txtf_content);
			content.requestFocus();
		}
	}
	
	/**
	 * @see com.twapime.app.activity.NewTweetActivity#post()
	 */
	@Override
	public void post() {
		final ProgressDialog progressDialog =
			ProgressDialog.show(
				this, "", getString(R.string.sending_dm), false);
		//
		new Thread() {
			@Override
			public void run() {
				EditText content =
					(EditText)findViewById(R.id.new_tweet_txtf_content);
				//
				String username = recipient.getText().toString();
				if (username.startsWith("@")) {
					username = username.substring(1);
				}
				//
				Tweet tweet =
					new Tweet(username, content.getEditableText().toString());
				//
				try {
					ter.send(tweet);
					//
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							progressDialog.dismiss();
						}
					});
					//
					finish();
				} catch (final Exception e) {
					//
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							progressDialog.dismiss();
							//
							UIUtil.showAlertDialog(
								NewDirectMessageActivity.this, e);
						}
					});
				}
			}
		}.start();
	}
}

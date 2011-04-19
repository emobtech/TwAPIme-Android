package com.twapime.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.twapime.app.R;
import com.twapime.app.TwAPImeApplication;
import com.twapime.app.util.UIUtil;
import com.twitterapime.rest.TweetER;
import com.twitterapime.search.Tweet;

/**
 * @author ernandes@gmail.com
 */
public class NewTweetActivity extends Activity {
	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		setContentView(R.layout.new_tweet);
		//
		final Button btnPost = (Button)findViewById(R.id.new_tweet_btn_post);
		btnPost.setEnabled(false);
		btnPost.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				post();
			}
		});
		//
		EditText t = (EditText)findViewById(R.id.new_tweet_txtf_content);
		t.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				TextView tv =
					(TextView)findViewById(R.id.new_tweet_txtv_number_chars);
				//
				tv.setText(s.length() + "");
				btnPost.setEnabled(s.length() > 0);
			}
		});
	}
	
	/**
	 * 
	 */
	public void post() {
		final ProgressDialog progressDialog =
			ProgressDialog.show(
				this, "", getString(R.string.posting_tweet), false);
		//
		final TwAPImeApplication app = (TwAPImeApplication)getApplication();
		final EditText t = (EditText)findViewById(R.id.new_tweet_txtf_content);
		//
		new Thread() {
			@Override
			public void run() {
				TweetER ter = TweetER.getInstance(app.getUserAccountManager());
				Tweet tweet = new Tweet(t.getEditableText().toString());
				//
				try {
					ter.post(tweet);
					//
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							progressDialog.dismiss();
						}
					});
					//
					setResult(HomeActivity.NEW_TWEET_RESULT);
					finish();
				} catch (final Exception e) {
					//
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							progressDialog.dismiss();
							//
							UIUtil.showAlertDialog(NewTweetActivity.this, e);
						}
					});
				}
			}
		}.start();
	}
}

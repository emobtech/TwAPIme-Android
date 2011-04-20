package com.twapime.app.widget;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.twapime.app.R;
import com.twapime.app.util.DateUtil;
import com.twitterapime.model.MetadataSet;
import com.twitterapime.rest.UserAccount;
import com.twitterapime.search.Tweet;

/**
 * @author ernandes@gmail.com
 */
public class TimelineArrayAdapter extends ArrayAdapter<Tweet> {
	/**
	 * 
	 */
	private Context context;

	/**
	 * 
	 */
	private List<Tweet> tweets;
	
	/**
	 * @param context
	 * @param textViewResourceId
	 * @param tweets
	 */
	public TimelineArrayAdapter(Context context, int textViewResourceId,
		List<Tweet> tweets) {
		super(context, textViewResourceId, tweets);
		//
		this.context = context;
		this.tweets = tweets;
	}
	
	/**
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi =
            	(LayoutInflater)context.getSystemService(
            		Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.tweet_row, null);
        }
        //
        Tweet tweet = tweets.get(position);
        TextView tv = (TextView) v.findViewById(R.id.tweet_row_txtv_username);
        UserAccount ua = tweet.getUserAccount();
        //
        if (ua != null) {
        	tv.setText(ua.getString(MetadataSet.USERACCOUNT_NAME));	
        } else {
        	tv.setText(tweet.getString(MetadataSet.TWEET_AUTHOR_NAME));
        }
        //
        tv = (TextView) v.findViewById(R.id.tweet_row_txtv_content);
        tv.setText(tweet.getString(MetadataSet.TWEET_CONTENT));
        //
        tv = (TextView) v.findViewById(R.id.tweet_row_txtv_time);
        tv.setText(
        	DateUtil.formatTweetDate(
        		Long.parseLong(
        			tweet.getString(MetadataSet.TWEET_PUBLISH_DATE)),
        			context));
        //
		return v;
	}
}

package com.twapime.app.widget;

import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.twapime.app.R;
import com.twapime.app.util.AsyncImageLoader;
import com.twapime.app.util.AsyncImageLoader.ImageCallback;
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
	 * 
	 */
	private AsyncImageLoader imageLoader;
	
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
		//
		imageLoader = new AsyncImageLoader();
	}
	
	/**
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {
        View rowView = convertView;
        RowViewCache rowViewCache;
        //
        if (rowView == null) {
            LayoutInflater vi =
            	(LayoutInflater)context.getSystemService(
            		Context.LAYOUT_INFLATER_SERVICE);
            //
            rowView = vi.inflate(R.layout.tweet_row, null);
            rowViewCache = new RowViewCache(rowView);
            rowView.setTag(rowViewCache);
        } else {
        	rowViewCache = (RowViewCache)rowView.getTag();
        }
        //
        Tweet tweet = tweets.get(position);
        UserAccount ua = tweet.getUserAccount();
        //
        TextView tv = rowViewCache.getUsernameView();
        //
        if (ua != null) {
        	tv.setText(ua.getString(MetadataSet.USERACCOUNT_NAME));	
        } else {
        	tv.setText(tweet.getString(MetadataSet.TWEET_AUTHOR_NAME));
        }
        //
        tv = rowViewCache.getContentView();
        tv.setText(tweet.getString(MetadataSet.TWEET_CONTENT));
        //
        tv = rowViewCache.getTimeView();
        tv.setText(
        	DateUtil.formatTweetDate(
        		Long.parseLong(
        			tweet.getString(MetadataSet.TWEET_PUBLISH_DATE)),
        			context));
        //
        ImageView imgView =	rowViewCache.getAvatarView();
        String imageUrl = ua.getString(MetadataSet.USERACCOUNT_PICTURE_URI);
        //
        imgView.setTag(imageUrl);
        //
        Drawable cachedImage = imageLoader.loadDrawable(imageUrl, new ImageCallback() {
            public void imageLoaded(Drawable imageDrawable, String imageUrl) {
                ImageView imageViewByTag =
                	(ImageView)((ListActivity)context).getListView().findViewWithTag(imageUrl);
                //
                if (imageViewByTag != null) {
                	Log.d("twapime", "imageLoaded: " + imageUrl);
                    imageViewByTag.setImageDrawable(imageDrawable);
                    notifyDataSetChanged();
                }
            }
        });
        imgView.setImageDrawable(cachedImage);
        //
		return rowView;
	}
	
	/**
	 * @author ernandes@gmail.com
	 */
	private class RowViewCache {
	    /**
	     * 
	     */
	    private View baseView;
	    
	    /**
	     * 
	     */
	    private TextView usernameView;
	    
	    /**
	     * 
	     */
	    private TextView contentView;
	    
	    /**
	     * 
	     */
	    private TextView timeView;
	    
	    /**
	     * 
	     */
	    private ImageView avatarView;
	 
	    /**
	     * @param baseView
	     */
	    public RowViewCache(View baseView) {
	        this.baseView = baseView;
	    }

		/**
		 * @return
		 */
		public TextView getUsernameView() {
	        if (usernameView == null) {
	        	usernameView =
	        		(TextView)baseView.findViewById(
	        			R.id.tweet_row_txtv_username);
	        }
	        return usernameView;
		}

		/**
		 * @return
		 */
		public TextView getContentView() {
	        if (contentView == null) {
	        	contentView =
	        		(TextView)baseView.findViewById(
	        			R.id.tweet_row_txtv_content);
	        }
	        //
			return contentView;
		}

		/**
		 * @return
		 */
		public TextView getTimeView() {
	        if (timeView == null) {
	        	timeView =
	        		(TextView)baseView.findViewById(R.id.tweet_row_txtv_time);
	        }
	        //
			return timeView;
		}

		/**
		 * @return
		 */
		public ImageView getAvatarView() {
	        if (avatarView == null) {
	        	avatarView =
	        		(ImageView)baseView.findViewById(R.id.tweet_row_img_avatar);
	        }
	        //
			return avatarView;
		}
	}
}

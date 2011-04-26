package com.twapime.app.widget;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.twapime.app.R;
import com.twitterapime.model.MetadataSet;
import com.twitterapime.rest.UserAccount;

/**
 * @author ernandes@gmail.com
 */
public class UserArrayAdapter extends ArrayAdapter<UserAccount> {
	/**
	 * 
	 */
	private Context context;

	/**
	 * 
	 */
	private List<UserAccount> users;
	
	/**
	 * @param context
	 * @param textViewResourceId
	 * @param users
	 */
	public UserArrayAdapter(Context context, int textViewResourceId,
		List<UserAccount> users) {
		super(context, textViewResourceId, users);
		//
		this.context = context;
		this.users = users;
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
            v = vi.inflate(R.layout.user_row, null);
        }
        //
        UserAccount user = users.get(position);
        //
        TextView tv = (TextView)v.findViewById(R.id.user_row_txtv_user_name);
       	tv.setText(user.getString(MetadataSet.USERACCOUNT_NAME));	
        //
        tv = (TextView) v.findViewById(R.id.user_row_txtv_username);
        tv.setText("@" + user.getString(MetadataSet.USERACCOUNT_USER_NAME));
        //
		return v;
	}
}

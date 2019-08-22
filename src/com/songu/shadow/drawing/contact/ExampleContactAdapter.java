package com.songu.shadow.drawing.contact;

import java.util.List;

import com.pkmmte.circularimageview.CircularImageView;
import com.songu.shadow.drawing.R;
import com.songu.shadow.drawing.model.UserProfile;
import com.songu.shadow.drawing.utils.Imageloader;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;



public class ExampleContactAdapter extends ContactListAdapter{

	public Imageloader imgLoader;
	public Context mContext;
	public ExampleContactAdapter(Context _context, int _resource,
			List<ContactItemInterface> _items) {
		super(_context, _resource, _items);
		this.mContext = _context;
		imgLoader = new Imageloader(_context);
		
	}
	
	// override this for custom drawing
	public void populateDataForRow(View parentView, ContactItemInterface item , int position){
		// default just draw the item only
		View infoView = parentView.findViewById(R.id.infoRowContainer);
		TextView fullNameView = (TextView)infoView.findViewById(R.id.fullNameView);
		TextView nicknameView = (TextView)infoView.findViewById(R.id.nickNameView);
		CircularImageView m = (CircularImageView) infoView.findViewById(R.id.userImg);
		
		nicknameView.setText(item.getItemForIndex());
		
		if(item instanceof UserProfile){
			UserProfile contactItem = (UserProfile)item;
			fullNameView.setText("Mail: " + contactItem.mEmail);
			m.setTag(contactItem.mImage);
			imgLoader.DisplayImage(contactItem.mImage, (Activity)mContext, m);
		}
		
	}

}

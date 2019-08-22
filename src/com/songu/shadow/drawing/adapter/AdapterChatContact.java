package com.songu.shadow.drawing.adapter;

import java.util.List;

import com.pkmmte.circularimageview.CircularImageView;
import com.songu.shadow.drawing.R;
import com.songu.shadow.drawing.contact.ContactItemInterface;
import com.songu.shadow.drawing.model.UserProfile;
import com.songu.shadow.drawing.utils.Imageloader;
import com.songu.shadow.drawing.utils.SyncImageLoader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AdapterChatContact extends BaseAdapter {

    public List<ContactItemInterface> m_lstItems;
    public int selItem = -1;
    public Context con;
    public Imageloader imgLoader;
    public AdapterChatContact(Context con)
    {
        super();
        this.con = con;
        imgLoader = new Imageloader(con);
    }
    public int getCount()
    {
        if (this.m_lstItems == null)
            return 0;
        return this.m_lstItems.size();
    }

    public ContactItemInterface getItem(int paramInt)
    {
        return this.m_lstItems.get(paramInt);
    }

    public long getItemId(int paramInt)
    {
        return 0L;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
        View localView = paramView;
        ViewHolder localViewHolder = null;

        if (localView == null)
        {
            localView = LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.example_contact_item, null);
        }
        else
        {
            localViewHolder = (ViewHolder) localView.getTag();
        }
        if (localViewHolder == null){
            localViewHolder = new ViewHolder();
            localViewHolder.m_photo = (CircularImageView) localView.findViewById(R.id.userImg);
            localViewHolder.m_name = ((TextView)localView.findViewById(R.id.nickNameView));
            localViewHolder.gps = ((ImageView)localView.findViewById(R.id.imageView5));
            localViewHolder.m_msg = ((TextView)localView.findViewById(R.id.fullNameView));
            localViewHolder.m_section = ((TextView) localView.findViewById(R.id.sectionTextView));
            localViewHolder.line = (LinearLayout) localView.findViewById(R.id.sectionLine);
            
            localView.setTag(localViewHolder);
        }
        //add String
        //ProfileModel t = m_lstItems.get(paramInt);
        //localViewHolder.m_item1.setImageURI(Uri.parse(t.image));
        //Bitmap bm = Globals.setImageScale(con, Uri.parse(t.image));
        //localViewHolder.m_item1.setImageBitmap(bm);
        localViewHolder.gps.setVisibility(View.INVISIBLE);
        localViewHolder.m_section.setVisibility(View.GONE);
        localViewHolder.line.setVisibility(View.GONE);
        UserProfile data = null;
        if (m_lstItems.get(paramInt) instanceof UserProfile)
        	data = (UserProfile)m_lstItems.get(paramInt);
        if (!data.mImage.equals(""))
        {
        	localViewHolder.m_photo.setTag(data.mImage);
            imgLoader.DisplayImage(data.mImage, (Activity)this.con, localViewHolder.m_photo);
        }
        localViewHolder.m_name.setText(data.mName);
        localViewHolder.m_msg .setText("");
        return localView;
    }

    public void update(List<ContactItemInterface> contactList)
    {
        this.m_lstItems = contactList;
        notifyDataSetChanged();
    }

    public class ViewHolder
    {
    	public TextView m_section;
        public CircularImageView m_photo;
        public TextView m_name;
        public ImageView gps;
        public TextView m_msg;
        public LinearLayout line;
    }

}


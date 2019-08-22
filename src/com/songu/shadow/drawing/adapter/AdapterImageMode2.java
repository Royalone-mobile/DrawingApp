package com.songu.shadow.drawing.adapter;

import java.util.List;

import com.songu.shadow.drawing.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AdapterImageMode2 extends BaseAdapter{
	
	  List<Bitmap> m_values;
	  Context con;
	  public AdapterImageMode2(Context con)
	  {
		  super();
		  this.con = con;		  
	  }
	  public int getCount()
	  {
	    if (this.m_values == null)
	      return 0;
	    return this.m_values.size();
	  }

	  public Bitmap getItem(int paramInt)
	  {
	    return this.m_values.get(paramInt);
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
	      localView = LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.item_home, null);
	     
	    }
	    else 
	    {
	    	localViewHolder = (ViewHolder) localView.getTag();
	    }
	    if (localViewHolder == null)
	    {
	    	 localViewHolder = new ViewHolder();
		     localViewHolder.imgItem = ((ImageView)localView.findViewById(R.id.itemHome));		     
		     localView.setTag(localViewHolder);
	    }
	    Bitmap showModel = this.m_values.get(paramInt);	  
	    localViewHolder.imgItem.setImageBitmap(showModel);
	    return localView;      
	    
	  }

	  public void update(List<Bitmap> values)
	  {
	    this.m_values = values;
	    notifyDataSetChanged();
	  }

	  public static class ViewHolder
	  {
		public ImageView imgItem;		
		
	  }
	
}

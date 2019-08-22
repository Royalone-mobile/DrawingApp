package com.songu.shadow.drawing.fragments;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.songu.shadow.drawing.doc.Globals;
import com.songu.shadow.drawing.model.CustomColor;
import com.songu.shadow.drawing.pref.AppPreferences;
import com.songu.shadow.drawing.tools.Tools;
import com.songu.shadow.drawing.ActivityPreviewColor;
import com.songu.shadow.drawing.MainActivity;
import com.songu.shadow.drawing.R;

public class ColorLibraryFragment extends CustomFragment{

	SwipeMenuListView listView;
	static LayoutInflater inflater;
	Context c;
	ArrayList<CustomColor> colors;
	ArrayList<CustomColor> favoritedColors;//only updated when needed
	ColorAdapter listAdapter;
	boolean showOnlyFavorites;
	int colorBeingEdited;
	
	public ColorLibraryFragment(LayoutInflater inflater, int resource) {
		super(inflater, resource);
		c =inflater.getContext();
		ColorLibraryFragment.inflater = inflater;
		colors = Tools.getSerializedColors(c);
		favoritedColors = Tools.getFavoritedColors(colors);
		setColors();
		listView = (SwipeMenuListView) fragmentLayout.findViewById(R.id.color_library_listview);
		listAdapter = new ColorAdapter();
		listView.setAdapter(listAdapter);
		listView.setMenuCreator(creator);
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				listView.smoothOpenMenu(position);
				return true;
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CustomColor colorPressed;
				
				if (showOnlyFavorites){
					colorPressed = favoritedColors.get(position);
				}else
					colorPressed = colors.get(position);
				//((MainActivity)c).openPreviewFragment(colorPressed.name);
				
				Globals.selColor = new CustomColor(colorPressed.color);
				Globals.selColor.name = colorPressed.name;
				
				Globals.selColor.location = colorPressed.location;
				
				
				//((ColorPreviewFragment)(((MainActivity)c).currentFragment)).setColor(colorPressed);
				Intent m = new Intent(ColorLibraryFragment.this.c,ActivityPreviewColor.class);
				c.startActivity(m);
				
			}
		});
		
		listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@SuppressLint("InflateParams")
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				
				CustomColor colorPressed;
				
				if (showOnlyFavorites){
					colorPressed = favoritedColors.get(position);
				}else
					colorPressed = colors.get(position);
				
				
				switch(index){
				case 0://share
					Intent sendIntent = new Intent();
					sendIntent.setAction(Intent.ACTION_SEND);
					sendIntent.putExtra(Intent.EXTRA_TEXT, colors.get(position).getDetails());
					sendIntent.setType("text/plain");
					c.startActivity(sendIntent);
					break;
					
				case 1://favorite
					if (showOnlyFavorites){
						boolean f =colors.get(colors.indexOf(favoritedColors.get(position))).favorited;
						colors.get(colors.indexOf(favoritedColors.get(position))).favorited = !f;
						favoritedColors.remove(position);
					}else
						colors.get(position).favorited = !colors.get(position).favorited;
					
					Tools.serializeColors(colors, c);
					delayedInvalidation();
					break;
					
				case 2://edit
					final LinearLayout dialogView = (LinearLayout) ColorLibraryFragment.inflater.inflate(R.layout.dialog_edit_color, null);
					((EditText)dialogView.findViewById(R.id.color_edit_edittext)).setText(colorPressed.name);
					((ImageView)dialogView.findViewById(R.id.edit_color_circle)).setColorFilter(colorPressed.color);
					colorBeingEdited = position;
					AlertDialog.Builder adb = new AlertDialog.Builder(c);
				    adb.setView(dialogView);
				    adb.setTitle(R.string.edit_color); 
				    adb.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() { 
				        public void onClick(DialogInterface dialog, int which) {
				        	if (showOnlyFavorites){
								colors.get(colors.indexOf(favoritedColors.get(colorBeingEdited))).name = ((EditText)dialogView.findViewById(R.id.color_edit_edittext)).getText().toString();
							}else
								colors.get(colorBeingEdited).name = ((EditText)dialogView.findViewById(R.id.color_edit_edittext)).getText().toString();
							
							Tools.serializeColors(colors, c);
							listAdapter.notifyDataSetChanged();
				      } }); 
				 
				 
				    adb.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) {
				 
				            dialog.dismiss();
				      } }); 
				    adb.show();

					break;
					
				case 3://delete
					removeItem(Tools.getViewByPosition(position, listView),position);
					break;
				}
				listAdapter.notifyDataSetChanged();
				return true;
			}
		});
		
		//checkColorSize();
		
	}
	
	
	@SuppressLint("InflateParams")
	public void checkColorSize(){
		if (colors.size() == 0){
			fragmentLayout.removeView(listView);
			fragmentLayout.addView(inflater.inflate(R.layout.color_library_none_item, null));
		}
	}
	
	private void delayedInvalidation() { 
		 
	    new Thread() {
	        public void run() { 
	                try { 
	                	Thread.sleep(300);
	                	
	                    ((Activity) c).runOnUiThread(new Runnable() {
	 
	                        @Override 
	                        public void run() { 
	                        	listAdapter.notifyDataSetChanged();
	                        } 
	                    }); 
	                    
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                } 
	            } 
	    }.start();
	}
	
	// To animate view slide out from right to left
	public void removeItem(View itemView, int pos){
		if (showOnlyFavorites){
			colors.remove(favoritedColors.get(pos));
			favoritedColors.remove(pos);
		}
		else
			colors.remove(pos);
		Tools.serializeColors(colors, c);
		TranslateAnimation animate = new TranslateAnimation(0,-itemView.getWidth(),0,0);
		animate.setDuration(500);
		itemView.startAnimation(animate);
		animate.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				animation.cancel();
				listAdapter.notifyDataSetChanged();
				checkColorSize();
			}
		});
	}
	
	
	public void setColors(){
//		for (int i = 0; i < 100; i++)
//			colors.add(Tools.getRandomColor());
	}
	
	
	SwipeMenuCreator creator = new SwipeMenuCreator() {

		@Override
		public void create(SwipeMenu menu) {
			//share item
	        SwipeMenuItem shareItem = new SwipeMenuItem(c);
	        shareItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,0xCE)));
	        shareItem.setWidth(Tools.numtodp(c, 50));
	        shareItem.setIcon(R.drawable.ic_social_share);
	        menu.addMenuItem(shareItem);
	        
	        //like item
	        SwipeMenuItem likeItem = new SwipeMenuItem(c);
	        likeItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,0xCE)));
	        likeItem.setWidth(Tools.numtodp(c, 50));
	        if (menu.getViewType()==0)
	        likeItem.setIcon(R.drawable.ic_action_favorite_outline);
	        else
	        	likeItem.setIcon(R.drawable.ic_action_favorite);
	        menu.addMenuItem(likeItem);
	        
	        //edit item
	        SwipeMenuItem editItem = new SwipeMenuItem(c);
	        editItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,0xCE)));
	        editItem.setWidth(Tools.numtodp(c, 50));
	        editItem.setIcon(R.drawable.ic_image_edit);
	        menu.addMenuItem(editItem);

	        //delete item
	        SwipeMenuItem deleteItem = new SwipeMenuItem(c);
	        deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,0x3F, 0x25)));
	        deleteItem.setWidth(Tools.numtodp(c, 50));
	        deleteItem.setIcon(R.drawable.ic_action_delete);
	        menu.addMenuItem(deleteItem);
		}
	};
	
	
	public class ColorAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			
			if (showOnlyFavorites)
				return favoritedColors.size();
			else
				return colors.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		
		
		@Override
        public int getViewTypeCount() {
            // menu type count
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            // current menu type
        	if (showOnlyFavorites){
        		if (favoritedColors.get(position).favorited)
        			return 1;
        	}else{
        		if (colors.get(position).favorited)
        			return 1;
        	}
        	
        	return 0;
        }
		
		class ViewHolder {  
	      public ImageView colorCircle;  
	      public TextView colorText;
	      public TextView dateText;
	      public TextView title;
	      public TextView location;
	    }  

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			CustomColor currentColor;
			
			if (showOnlyFavorites)
				currentColor = favoritedColors.get(position);
			else
				currentColor = colors.get(position);
			
		    View v = convertView;  
			ViewHolder viewHolder;  
            if (convertView == null) {
            	v = inflater.inflate(R.layout.color_library_item, null);  
                viewHolder = new ViewHolder();  
                viewHolder.colorCircle = (ImageView) v.findViewById(R.id.color_match_color_img);
                viewHolder.colorText = (TextView) v.findViewById(R.id.color_match_item_color);
                viewHolder.dateText = (TextView) v.findViewById(R.id.color_lib_date);
                viewHolder.title = (TextView) v.findViewById(R.id.color_match_item_title);
                viewHolder.location = (TextView) v.findViewById(R.id.color_lib_location);
                
                
                v.setTag(viewHolder);  
            } else {
                viewHolder = (ViewHolder) v.getTag();  
            }
            viewHolder.colorCircle.setColorFilter(currentColor.color);
            
            viewHolder.title.setSelected(true);
            viewHolder.colorText.setSelected(true);
            
           /* LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) viewHolder.colorCircle.getLayoutParams();
            if (Globals.g_screenHeight / 12 < 80)
            {
	     		p.width = Globals.g_screenHeight / 12 - 20;
	     		p.height = Globals.g_screenHeight / 12 - 20;
	     		
	     		viewHolder.colorCircle.setLayoutParams(p);
            }*/
            
            SpannableString rgb=  new SpannableString("RGB ");
            rgb.setSpan(new StyleSpan(Typeface.BOLD), 0, rgb.length(), 0); 
            
            SpannableString hex=  new SpannableString(" HEX ");
            hex.setSpan(new StyleSpan(Typeface.BOLD), 0, hex.length(), 0); 
            
            SpannableString cmyk=  new SpannableString(" CMYK ");
            cmyk.setSpan(new StyleSpan(Typeface.BOLD), 0, cmyk.length(), 0); 
            
            
            Tools.setColorTextData(colors.get(position), viewHolder.colorText);
            
            viewHolder.dateText.setText(currentColor.dateCaptured);
            
            viewHolder.title.setText(currentColor.name);
            viewHolder.title.setSelected(true);
            
            
            if (AppPreferences.geolocation){
	            if (currentColor.location!=null)
	            	viewHolder.location.setText(currentColor.location);
	            else
	            	viewHolder.location.setText(c.getResources().getString(R.string.unknown_location));
            }else
            	viewHolder.location.setVisibility(View.INVISIBLE);
            
            
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
     		        LayoutParams.MATCH_PARENT,      
     		       LayoutParams.MATCH_PARENT
     		);
     		params.setMargins(3, 10, 3, 10);
     		/*if (Globals.g_screenHeight / 12 > 80)
     		{
	     		params.height = Globals.g_screenHeight / 12;
	     		
	     		v.setLayoutParams(params);
     		}*/
     		
     		
            return v;  
		}
	}
	
	public void toggleShowFavorites(){
		favoritedColors = Tools.getFavoritedColors(colors);
		showOnlyFavorites = !showOnlyFavorites;
		listAdapter.notifyDataSetChanged();
	}
	
}

package com.songu.shadow.drawing.fragments;

import java.util.ArrayList;

import com.songu.shadow.drawing.R;
import com.songu.shadow.drawing.doc.Globals;
import com.songu.shadow.drawing.model.CustomColor;
import com.songu.shadow.drawing.tools.Tools;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ColorMatchFragment extends CustomFragment {

	
	LinearLayout m_layoutColorContainer;
	FrameLayout m_layoutContainer;
	LinearLayout m_layoutTitleContainer;
	LinearLayout m_layoutResultContainer;
	
	ScrollView m_scrList;
	View bottomColorView;
	ArrayList<CustomColor> libraryColors;
	ArrayList<CustomColor> currentColors = new ArrayList<CustomColor>();
	Context c;
	ImageView addButton;
	static LayoutInflater inflater;
	
	public ColorMatchFragment(LayoutInflater inflater, int resource) 
	{
		super(inflater, resource);
		ColorMatchFragment.inflater = inflater;
		addButton = (ImageView) fragmentLayout.findViewById(R.id.color_match_add).findViewById(R.id.color_match_color_img);
		
		this.m_layoutColorContainer = (LinearLayout)fragmentLayout.findViewById(R.id.layoutMatchColors);
		this.m_layoutContainer = (FrameLayout) fragmentLayout.findViewById(R.id.layoutFramePercent);
		this.m_layoutTitleContainer = (LinearLayout) fragmentLayout.findViewById(R.id.layoutMatchTitle);
		this.m_layoutResultContainer = (LinearLayout) fragmentLayout.findViewById(R.id.layoutMatchValue);
		
		bottomColorView = fragmentLayout.findViewById(R.id.color_match_bottom);
		c = inflater.getContext();
		libraryColors = Tools.getSerializedColors(c);
		//setCurrentColorsFromLib();
		updateUI();
		
		addButton.setOnClickListener(new OnClickListener() {
			
			@SuppressLint("InflateParams")
			@Override
			public void onClick(View v) {

				ListAdapter adapter = new ArrayAdapter<String>(
					    c.getApplicationContext(), R.layout.color_library_item) {
					 	
					    @Override
							public int getCount() {
								return libraryColors.size();
							}

						ViewHolder holder;
					    class ViewHolder { 
					        View color;
					        TextView title;
					    } 
					 
					    public View getView(int position, View convertView, ViewGroup parent) {
					 
					    if (convertView == null) {
					        convertView = ColorMatchFragment.inflater.inflate(
					        R.layout.color_match_add_from_lib_item, null);
					 
					        holder = new ViewHolder();
					        holder.color = (View) convertView
					        .findViewById(R.id.add_color);
					        holder.title = (TextView) convertView
					        .findViewById(R.id.add_name);
					        convertView.setTag(holder);
					    } else
					        holder = (ViewHolder) convertView.getTag();
					      
					 
					    holder.title.setText(libraryColors.get(position).name);
					    holder.color.setBackgroundColor(libraryColors.get(position).color);
					    return convertView;
					    } 
					}; 
				
				
				AlertDialog.Builder builder = new AlertDialog.Builder(c);
				 
				builder.setTitle(R.string.select_color);
				 
				builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int which) {
				        /*if (currentColors.size()==4){
				        	currentColors.remove(3);
				        }*/
				        
				        //currentColors.add(0,libraryColors.get(which));
				        currentColors.add(libraryColors.get(which));
				        addColorView(libraryColors.get(which));
				        
				        //updateUI();
				    } 
				 
				}); 
				 
				builder.create();
				if (! ((Activity) c).isFinishing()) {
				    builder.show();
				} 
				

				
			}
		});
	}
	
	
	
	public void setCurrentColorsFromLib(){
		for (int i = libraryColors.size() ; i >libraryColors.size()-5; i--){
			
			if (i < libraryColors.size() && i>=0)
				currentColors.add(libraryColors.get(i));
		}
		
	}
	
	public int[] getCurrentColorIntArray(){
		int[] colorArray = new int[currentColors.size()];
		
		for (int i = 0; i < currentColors.size(); i++){
			Log.d("SC","color"+i+currentColors.get(i).color);
			colorArray[i] = currentColors.get(i).color;
		}
		
		return colorArray;
	}
	
	public void updateUI(){
		//addColorView();		
		colorBottom();
	}
	public void addMatchResult(int i)
	{
		TextView m_view = new TextView(inflater.getContext());	 		
 		TextView m_viewResult = new TextView(inflater.getContext());
 		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(0,LayoutParams.MATCH_PARENT);
 		LinearLayout.LayoutParams  params3 = new LinearLayout.LayoutParams(0,LayoutParams.MATCH_PARENT);
 		
 		params2.weight = 1;
 		params3.weight = 1;
 		m_viewResult.setGravity(Gravity.CENTER);
 		m_view.setGravity(Gravity.CENTER);
 		
 		m_view.setLayoutParams(params2);	 
 		m_viewResult.setLayoutParams(params3);
 		
 		m_view.setText("MATCH" + String.valueOf(i - 1));
 		m_viewResult.setText(String.valueOf(Tools.getColorPercentageDifference(currentColors.get(currentColors.size() - 2), currentColors.get(currentColors.size() - 1))) + "%");
 		
 		m_view.setTextColor(inflater.getContext().getResources().getColor(R.color.color_white));
 		m_viewResult.setTextColor(inflater.getContext().getResources().getColor(R.color.color_white));
 		m_view.setTextSize(20);
 		m_viewResult.setTextSize(20);
 		
 		this.m_layoutTitleContainer.addView(m_view);
 		this.m_layoutResultContainer.addView(m_viewResult);
	}
	public void addColorView(CustomColor color)
	{
		View m_view = new View(inflater.getContext());	 		
 		m_view = LayoutInflater.from(inflater.getContext()).inflate(R.layout.color_match_item, null);
 		LayoutParams params = new LayoutParams(
 		        LayoutParams.MATCH_PARENT,      
 		       LayoutParams.WRAP_CONTENT
 		);
 		params.setMargins(3, 10, 3, 10);
 		params.height = Globals.g_screenHeight / 8;
 		MatchColorItem viewgroup = new MatchColorItem();
 		viewgroup.m_imgColor = (ImageView) m_view.findViewById(R.id.color_match_color_img);
 		
 		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewgroup.m_imgColor.getLayoutParams();
 		lp.width = Globals.g_screenHeight / 8;
 		viewgroup.m_imgColor.setLayoutParams(lp);
 		viewgroup.m_txtInfo = (TextView) m_view.findViewById(R.id.color_match_item_title);
 		viewgroup.m_txtName = (TextView) m_view.findViewById(R.id.color_match_item_color);
 		
 		m_view.setTag(viewgroup);
 		m_view.setLayoutParams(params);	 
 		
 		viewgroup.m_imgColor.setColorFilter(color.color);
 		viewgroup.m_txtInfo.setText(color.name);
 		Tools.setColorTextData(color,viewgroup.m_txtName);
 		viewgroup.m_txtName.setSelected(true);
 		
 		this.m_layoutColorContainer.addView(m_view);
 		
 		if (this.currentColors.size() > 1)
 		{
 			addPercentView(currentColors.size());
 			addMatchResult(currentColors.size());
 		}
 		
 		updateUI();
	}
	public void addPercentView(int i)
	{
		View m_view = new View(inflater.getContext());	 		
 		m_view = LayoutInflater.from(inflater.getContext()).inflate(R.layout.color_match_item_percent, null);
 		FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(
 		        LayoutParams.MATCH_PARENT,      
 		       LayoutParams.WRAP_CONTENT
 		);
 		params1.setMargins(15 + Globals.g_screenHeight / 16 - Globals.g_screenHeight / 30 , (int) (this.m_layoutColorContainer.getY() + (20 +Globals.g_screenHeight / 8 )* (i - 1) - (Globals.g_screenHeight / 30)), 3, 10);
 		params1.height = Globals.g_screenHeight / 15;
 		params1.width = Globals.g_screenHeight / 15;
 		
 		PercentItem viewgroup = new PercentItem();
 		viewgroup.m_imgCircle = (ImageView) m_view.findViewById(R.id.color_match_color_percentimg);
 		
 		FrameLayout.LayoutParams pp = (FrameLayout.LayoutParams) viewgroup.m_imgCircle.getLayoutParams();
 		pp.width = Globals.g_screenHeight / 15;
 		pp.height = Globals.g_screenHeight / 15;
 		//pp.setMargins(10 + Globals.g_screenWidth / 16 -Globals.g_screenHeight / 30 , (int) (this.m_layoutColorContainer.getY() + (20 +Globals.g_screenHeight / 8 )* (i - 1) - (Globals.g_screenHeight / 30)), 3, 10);
 		viewgroup.m_imgCircle.setLayoutParams(pp);
 		viewgroup.m_txtPercent = (TextView) m_view.findViewById(R.id.color_match_item_small_percent); 		
 		
 		m_view.setTag(viewgroup);
 		m_view.setLayoutParams(params1);	 
 		
 		viewgroup.m_txtPercent.setText(String.valueOf(Tools.getColorPercentageDifference(currentColors.get(currentColors.size() - 2), currentColors.get(currentColors.size() - 1))) + "%");
 		
 		
 		this.m_layoutContainer.addView(m_view);
	}
	public void setColorItems(){
		/*for (int i = 0; i< currentColors.size(); i ++){
			
			switch (i){
			case 0:
				((ImageView) fragmentLayout.findViewById(R.id.color_match_color1).findViewById(R.id.color_match_color_img)).setColorFilter(currentColors.get(i).color);
				((TextView) fragmentLayout.findViewById(R.id.color_match_color1).findViewById(R.id.color_match_item_title)).setText(currentColors.get(i).name);
				Tools.setColorTextData(currentColors.get(i), ((TextView) fragmentLayout.findViewById(R.id.color_match_color1).findViewById(R.id.color_match_item_color)));
			break;
				
			case 1:
				((ImageView) fragmentLayout.findViewById(R.id.color_match_color2).findViewById(R.id.color_match_color_img)).setColorFilter(currentColors.get(i).color);
				((TextView) fragmentLayout.findViewById(R.id.color_match_color2).findViewById(R.id.color_match_item_title)).setText(currentColors.get(i).name);
				Tools.setColorTextData(currentColors.get(i), ((TextView) fragmentLayout.findViewById(R.id.color_match_color2).findViewById(R.id.color_match_item_color)));
				((TextView) fragmentLayout.findViewById(R.id.color_match_percent1)).setText(""+Tools.getColorPercentageDifference(currentColors.get(0), currentColors.get(1))+"%");
				((TextView) fragmentLayout.findViewById(R.id.color_match_small_percent1).findViewById(R.id.color_match_item_small_percent)).setText(""+Tools.getColorPercentageDifference(currentColors.get(0), currentColors.get(1))+"%");
			break;
			
			case 2:
				((ImageView) fragmentLayout.findViewById(R.id.color_match_color3).findViewById(R.id.color_match_color_img)).setColorFilter(currentColors.get(i).color);
				((TextView) fragmentLayout.findViewById(R.id.color_match_color3).findViewById(R.id.color_match_item_title)).setText(currentColors.get(i).name);
				Tools.setColorTextData(currentColors.get(i), ((TextView) fragmentLayout.findViewById(R.id.color_match_color3).findViewById(R.id.color_match_item_color)));
				((TextView) fragmentLayout.findViewById(R.id.color_match_percent2)).setText(""+Tools.getColorPercentageDifference(currentColors.get(1), currentColors.get(2))+"%");
				((TextView) fragmentLayout.findViewById(R.id.color_match_small_percent2).findViewById(R.id.color_match_item_small_percent)).setText(""+Tools.getColorPercentageDifference(currentColors.get(1), currentColors.get(2))+"%");
			break;
			
			case 3:
				((ImageView) fragmentLayout.findViewById(R.id.color_match_color4).findViewById(R.id.color_match_color_img)).setColorFilter(currentColors.get(i).color);
				((TextView) fragmentLayout.findViewById(R.id.color_match_color4).findViewById(R.id.color_match_item_title)).setText(currentColors.get(i).name);
				Tools.setColorTextData(currentColors.get(i), ((TextView) fragmentLayout.findViewById(R.id.color_match_color4).findViewById(R.id.color_match_item_color)));
				((TextView) fragmentLayout.findViewById(R.id.color_match_percent3)).setText(""+Tools.getColorPercentageDifference(currentColors.get(2), currentColors.get(3))+"%");
				((TextView) fragmentLayout.findViewById(R.id.color_match_small_percent3).findViewById(R.id.color_match_item_small_percent)).setText(""+Tools.getColorPercentageDifference(currentColors.get(2), currentColors.get(3))+"%");
			break;
			}
		}
		
		
		*/
		
		
	}
	
	@SuppressWarnings("deprecation")
	public void colorBottom(){
		Log.d("SC", "S"+currentColors.size());
		if (currentColors !=null && currentColors.size() > 1 ){
			ShapeDrawable.ShaderFactory shaderFactory = new ShapeDrawable.ShaderFactory() {
			    @Override 
			    public Shader resize(int width, int height) {
			    	
			    	float[] pos = new float[currentColors.size()];
			    	for (int i = 0;i < currentColors.size();i++)
			    	{
			    		pos[i] = i * ((float)1 / (currentColors.size() - 1));
			    	}
			    	
			        LinearGradient linearGradient = new LinearGradient(0, 0, width, height, 
			        	getCurrentColorIntArray(), //substitute the correct colors for these 
			            pos, 
			            Shader.TileMode.REPEAT);
			         return linearGradient;
			    } 
			}; 
			PaintDrawable paint = new PaintDrawable();
			paint.setShape(new RectShape());
			paint.setShaderFactory(shaderFactory);
			bottomColorView.setBackgroundDrawable(paint);
		}else{
			bottomColorView.setBackgroundColor(Color.DKGRAY);
		}
	}
	class PercentItem
	{
		public ImageView m_imgCircle;
		public TextView m_txtPercent;
	}
	class MatchColorItem
	{
		public ImageView m_imgColor;
		public TextView m_txtName;
		public TextView m_txtInfo;
	}
}

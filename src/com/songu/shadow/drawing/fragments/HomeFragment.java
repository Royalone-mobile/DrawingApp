package com.songu.shadow.drawing.fragments;



import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;













import ui.containers.FeatureCoverFlow;

import com.songu.shadow.drawing.MainActivity;
import com.songu.shadow.drawing.OpenDrawingActivity;
import com.songu.shadow.drawing.R;
import com.songu.shadow.drawing.adapter.AdapterImageHome;
import com.songu.shadow.drawing.adapter.AdapterImageMode2;
import com.songu.shadow.drawing.control.CoverFlow;
import com.songu.shadow.drawing.doc.Globals;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Bitmap.Config;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

import com.songu.shadow.drawing.tools.SerializationTools;
import com.songu.shadow.drawing.tools.Tools;
import com.songu.shadow.drawing.widget.MultiViewPager;
import com.songu.shadow.drawing.widget.RoundedImageView;




@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class HomeFragment  extends CustomFragment implements View.OnClickListener,View.OnTouchListener {
	
	public FrameLayout controlLayout;
	public AdapterImageHome adapterImage;
	public ViewPager viewPager;
	public ArrayList<ImageView> m_imgList;
	public List<Fragment> lst;
	public LinearLayout layoutDot;
	public LinearLayout layoutCover;
	public int pre = 0;
	public Button btnNew;
	public Button btnGallery;
	public Button btnDelete;
	int currentPosition;
	public Context mContext;
	public GridView mGridView;
	public FeatureCoverFlow coverFlow;
	public int currentMode = 0;
	public AdapterImageMode2 adapterGrid;
	public List<ImageView> mDots;
	
	public HomeFragment(LayoutInflater inflater, int resource) {
		super(inflater, resource);
		layoutDot = (LinearLayout) fragmentLayout.findViewById(R.id.layoutHomeDotbar);
		layoutCover = (LinearLayout) fragmentLayout.findViewById(R.id.layoutCoverImage);
		adapterImage = new AdapterImageHome(inflater.getContext());
		mContext = inflater.getContext();
		btnNew = (Button) fragmentLayout.findViewById(R.id.btnNew);
		btnGallery = (Button) fragmentLayout.findViewById(R.id.btnMode);
		btnDelete = (Button) fragmentLayout.findViewById(R.id.btnDelete);
		mGridView = (GridView) fragmentLayout.findViewById(R.id.grdGallery);
		mGridView.setVisibility(View.GONE);
		btnNew.setOnClickListener(this);
		btnGallery.setOnClickListener(this);
		adapterGrid = new AdapterImageMode2(mContext);
		this.mGridView.setAdapter(adapterGrid);
		btnDelete.setOnClickListener(this);
		getImage();
		coverFlow = (FeatureCoverFlow)fragmentLayout.findViewById(R.id.featureCoverFlow2);
		
		
		
		if (adapter.getCount()>0)
		{
		//coverFlow.setSpacing(0.5f);
		coverFlow.setAdapter(adapter);
		coverFlow.setMaxScaleFactor(1.5f);
		//coverFlow.setScalingThreshold(1f);
		//coverFlow.setSpacing(1f);
		coverFlow.setMaxRotationAngle(10f);
		
		coverFlow.setOnScrollPositionListener(new FeatureCoverFlow.OnScrollPositionListener() {
            @Override
            public void onScrolledToPosition(int position) {
                //TODO CoverFlow stopped to position
            	currentPosition = position;
            	Log.d("sc", "P"+currentPosition);
            	drawDot(currentPosition);
            }

            @Override
            public void onScrolling() {
                //TODO CoverFlow began scrolling
            }
        });
		
		coverFlow.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long id) {
				
				openBitmap(pos % images.size());
				
			}
		});
		
		}else{
			noDrawings();
		}
		
		
		
		
		final MultiViewPager pager = (MultiViewPager) fragmentLayout.findViewById(R.id.pager);		
		
		CoverFlow coverFlow;
		coverFlow = new CoverFlow(mContext,this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);		
		coverFlow.setLayoutParams(lp);
		
		this.layoutCover.addView(coverFlow);
	    //coverFlow.setAdapter(new AdapterImageHome(mContext));

	    AdapterImageHome coverImageAdapter =  new AdapterImageHome(mContext);
	    
	    
	    coverFlow.setAdapter(coverImageAdapter);
	    
	    mDots = new ArrayList<ImageView>();
	    //coverFlow.setSelection(4, true);
	    if (images != null)
	    {
	    	coverImageAdapter.update(images);
	    	coverFlow.setSelection(images.size() / 2,true);
	    	
	    	adapterGrid.update(images);
	    	
	    	for (int i = 0;i < images.size();i++)
	    	{
	    		ImageView mDot = new ImageView(mContext);
	    		mDot.setImageDrawable(mContext.getResources().getDrawable(R.drawable.unselect));
	    		mDots.add(mDot);
	    		this.layoutDot.addView(mDot);
	    	}
	    }
	    
	    coverFlow.setAnimationDuration(1000);
	    
	    
		//coverFlow.seton
	    coverFlow.setOnTouchListener(this);
	    coverFlow.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated meth	od stub
				openBitmap(arg2);
				
			}
		});
	    coverFlow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				currentPosition = arg2;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	    mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				openBitmap(arg2);
			}
		});
	    mGridView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				currentPosition = arg2;
				drawDot(currentPosition);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				currentPosition = 0;
				drawDot(currentPosition);
				
			}
		});
	    
	}
	public void openBitmap(int arg2)
	{
		flag = true;
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;
		Bitmap mp = BitmapFactory.decodeFile(imageFiles.get(arg2).getAbsolutePath(),options);
		
		
		//Globals.back = Bitmap.createBitmap(images.get(arg2).getWidth(),images.get(arg2).getHeight(),Config.ARGB_4444);
		
		Globals.back = mp.copy(Bitmap.Config.ARGB_8888, true);
		
		//Canvas c = new Canvas(Globals.back);
		//c.drawBitmap(images.get(arg2),0,0,null);
		Globals.viewNumber = 9;
		MainActivity activity = (MainActivity) mContext;
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		activity.setTitle("");
		activity.getActionBar().setBackgroundDrawable(null);
		activity.getActionBar().setDisplayShowCustomEnabled(true);
		RelativeLayout abContent = (RelativeLayout) inflater.inflate(R.layout.draw_actionbar_contents, null);
		activity.getActionBar().setCustomView(inflater.inflate(R.layout.draw_actionbar_contents, null));
		if (currentFragment == null)
			currentFragment = new DrawFragment(inflater, R.layout.fragment_draw, null);
		
		activity.frameMain.removeAllViews();
		activity.frameMain.addView(currentFragment.fragmentLayout,0);
		activity.currentFragment = currentFragment;
		mp = null;
		freeMemory();
	}
	public void drawDot(int t)
	{
		for (int i = 0;i < mDots.size();i++)
		{
			if (i == t)
				mDots.get(i).setImageDrawable(mContext.getResources().getDrawable(R.drawable.select));
			else
				mDots.get(i).setImageDrawable(mContext.getResources().getDrawable(R.drawable.unselect));
		}
	}
	public void noDrawings(){
		/*contentView.removeView(coverFlow);
		contentView.removeView(findViewById(R.id.open_drawing_bottom));
		TextView noDrawings = new TextView(this);
		noDrawings.setText(R.string.no_drawings);
		noDrawings.setGravity(Gravity.CENTER);
		noDrawings.setPadding(0, Tools.numtodp(this, 20), 0, 0);
		noDrawings.setTextSize(Tools.numtodp(this, 18));
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		contentView.addView(noDrawings,lp);*/
	}
	public void resizePages(int position)
	{
		//Top Image
		for (int i = 0;i < lst.size();i++)
		{
			if (lst.size() > i)
			{
				View v = lst.get(i).getView();
				if (v != null)
				{
					if (i == position)
					{
						FrameLayout frame = (FrameLayout)lst.get(i).getView().findViewById(R.id.vg_cover);
						RelativeLayout.LayoutParams fp= (RelativeLayout.LayoutParams)frame.getLayoutParams();		
						fp.width = Globals.g_screenWidth / 5;
						fp.height = (int)(fp.width * 1.5);
						frame.setLayoutParams(fp);
						
					}
					else if (Math.abs(i - position) == 1)
					{
						FrameLayout frame = (FrameLayout)lst.get(i).getView().findViewById(R.id.vg_cover);
						RelativeLayout.LayoutParams fp= (RelativeLayout.LayoutParams)frame.getLayoutParams();		
						fp.width = Globals.g_screenWidth / 6;
						fp.height = (int)(fp.width * 1.5);
						frame.setLayoutParams(fp);
					}
					else if (Math.abs(i - position) == 2)
					{				 
						FrameLayout frame = (FrameLayout)lst.get(i).getView().findViewById(R.id.vg_cover);
						RelativeLayout.LayoutParams fp= (RelativeLayout.LayoutParams)frame.getLayoutParams();		
						fp.width = Globals.g_screenWidth / 8;
						fp.height = (int)(fp.width * 1.5);
						frame.setLayoutParams(fp);
					}
					else if (Math.abs(i - position) == 3)
					{
						
							FrameLayout frame = (FrameLayout)lst.get(i).getView().findViewById(R.id.vg_cover);
							RelativeLayout.LayoutParams fp= (RelativeLayout.LayoutParams)frame.getLayoutParams();		
							fp.width = Globals.g_screenWidth / 12;
							fp.height = (int)(fp.width * 1.5);
							frame.setLayoutParams(fp);
						
					}
					else
					{
						
							FrameLayout frame = (FrameLayout)lst.get(i).getView().findViewById(R.id.vg_cover);
							RelativeLayout.LayoutParams fp= (RelativeLayout.LayoutParams)frame.getLayoutParams();		
							fp.width = Globals.g_screenWidth / 15;
							fp.height = (int)(fp.width * 1.5);
							frame.setLayoutParams(fp);
						
					}
				}
			}
		}
		
		
		
		
	}
	public void initView(View view)
	{
		//this.controlLayout = (FrameLayout) view.findViewById(R.id.controlLayout);
		//slideControl = new SlideRoundImageControl(this.getActivity(),this.controlLayout,Globals.g_screenWidth,Globals.g_screenHeight);
		
	}
	public DrawFragment currentFragment;
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId())
		{
		case R.id.btnNew:  //Open Draw Screen
			Globals.viewNumber = 9;
			MainActivity activity = (MainActivity) mContext;
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			activity.setTitle("");
			activity.getActionBar().setBackgroundDrawable(null);
			activity.getActionBar().setDisplayShowCustomEnabled(true);
			RelativeLayout abContent = (RelativeLayout) inflater.inflate(R.layout.draw_actionbar_contents, null);
			activity.getActionBar().setCustomView(inflater.inflate(R.layout.draw_actionbar_contents, null));
			if (currentFragment == null)
				currentFragment = new DrawFragment(inflater, R.layout.fragment_draw, null);
			this.freeMemory();
			activity.frameMain.removeAllViews();
			activity.frameMain.addView(currentFragment.fragmentLayout,0);
			activity.currentFragment = currentFragment;
			break;
		case R.id.btnMode: //Change Mode
			if (currentMode == 0)
			{
				currentMode = 1;
				this.mGridView.setVisibility(View.VISIBLE);
				this.coverFlow.setVisibility(View.GONE);
				//this.layoutCover.setVisibility(View.GONE);
				btnGallery.setBackgroundDrawable(this.mContext.getResources().getDrawable(R.drawable.buttonmode1));
				
			}
			else 
			{
				currentMode = 0;
				this.mGridView.setVisibility(View.GONE);
				this.coverFlow.setVisibility(View.VISIBLE);
				//this.layoutCover.setVisibility(View.VISIBLE);
				btnGallery.setBackgroundDrawable(this.mContext.getResources().getDrawable(R.drawable.buttonmode));
			}
			break;
		case R.id.btnDelete://Delete Selected Item
			SerializationTools.deletePoints(mContext,imageFiles.get(currentPosition).getName());
			imageFiles.get(currentPosition).delete();
			imageFiles.remove(currentPosition);
			images.remove(currentPosition);
			
			if (imageFiles.size()==0){
				noDrawings();
			}else{
				this.adapterImage.update(images);
			}
			break;
		}
	}
	public void freeMemory()
	{
		if (this.m_imgList != null)
		{
			for (int i = 0;i < this.m_imgList.size();i++)
			{
				this.m_imgList.get(i).setImageDrawable(null);
			}
		}
		for (int i = 0;i <this.adapterImage.mImageIds.length;i++)
		{
			this.adapterImage.mImageIds[i] = null;
		}
		if (images != null)
		{
			for (int i = 0;i < this.images.size();i++)
			{
				this.images.get(i).recycle();
				this.images.set(i,null);
			}
			for (int i = 0;i < imageFiles.size();i++)
			{
				imageFiles.set(i, null);
			}
		}
		this.imageFiles = null;
		this.images = null;
		this.m_imgList = null;
		System.gc();
	}
	
	
	ArrayList<File> imageFiles;
	ArrayList<Bitmap> images;
	public void getImages(){
		 
		 if ((imageFiles!=null && images == null) || 
				 (imageFiles!=null && images != null && imageFiles.size()!=images.size())){
			 
			 images = new ArrayList<Bitmap>();
			 
			 BitmapFactory.Options options = new BitmapFactory.Options();
			 options.inSampleSize = 3;
			 
			 for (int i = 0; i< imageFiles.size(); i ++){
				 images.add(BitmapFactory.decodeFile(imageFiles.get(i).getAbsolutePath(),options));
			 }
			 
		 }
			
	}
	public void getImage()
	{
		File dir = new File(mContext.getApplicationInfo().dataDir+"/Drawings/");
		
		if (dir.exists()){
			imageFiles = new ArrayList<File>();
			
			
			File[] files  = dir.listFiles();
			for (int i = 0; i < files.length; i++){
				imageFiles.add(files[i]);
				files[i] = null;
			}
			
			getImages();
		}
	}
	public BaseAdapter adapter = new BaseAdapter() {
		
		
		 class ViewHolder {  
		      public RoundedImageView image;  
		 } 
		 
		 
		 
		 public void getImages(){
			 
			 if ((imageFiles!=null && images == null) || 
					 (imageFiles!=null && images != null && imageFiles.size()!=images.size())){
				 
				 images = new ArrayList<Bitmap>();
				 
				 BitmapFactory.Options options = new BitmapFactory.Options();
				 options.inSampleSize = 2;
				 
				 for (int i = 0; i< imageFiles.size(); i ++){
					 images.add(BitmapFactory.decodeFile(imageFiles.get(i).getAbsolutePath(),options));
				 }
				 
			 }
				
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View v = convertView;  
			ViewHolder viewHolder;
	           if (convertView == null) {   
	                v = new RoundedImageView(mContext); 
	                viewHolder = new ViewHolder();
	                viewHolder.image = (RoundedImageView) v;
	                v.setTag(viewHolder);  
	           } else {  
	                viewHolder = (ViewHolder) v.getTag();  
	           }  
	           viewHolder.image.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.drawable_home_gallery_stroke));
	           viewHolder.image.setImageBitmap(images.get(position));
	           return v; 
		}
		
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			File dir = new File(mContext.getApplicationInfo().dataDir+"/Drawings/");
			
			if (dir.exists()){
				imageFiles = new ArrayList<File>();
				
				
				File[] files  = dir.listFiles();
				for (int i = 0; i < files.length; i++){
					imageFiles.add(files[i]);
					files[i] = null;
				}
				
				getImages();
				return files.length;
			}
			else
				return 0;
		}
	};
	boolean flag = false;
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return flag;
	}
	
	

}

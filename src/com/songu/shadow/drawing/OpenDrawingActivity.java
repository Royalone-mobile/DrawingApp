package com.songu.shadow.drawing;

import java.io.File;
import java.util.ArrayList;
import ui.containers.FeatureCoverFlow;
import com.songu.shadow.drawing.tools.SerializationTools;
import com.songu.shadow.drawing.tools.Tools;
import com.songu.shadow.drawing.view.CustomToolbar;
import com.songu.shadow.drawing.widget.RoundedImageView;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class OpenDrawingActivity extends FragmentActivity{
	FeatureCoverFlow coverFlow;
	
	ImageView deleteButton;
	int currentPosition;
	LinearLayout contentView;
	//CustomToolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_open_drawing);
		contentView = (LinearLayout) findViewById(R.id.open_drawing_cv);
		coverFlow = (FeatureCoverFlow)findViewById(R.id.featureCoverFlow1);
		deleteButton = (ImageView) findViewById(R.id.preview_delete);
		
		
		//setSupportActionBar(toolbar);
		this.getActionBar().setHomeButtonEnabled(true);
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		
		
		
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
				Intent returnIntent = new Intent();
				returnIntent.putExtra("filename",imageFiles.get(currentPosition).getName());
				setResult(RESULT_OK, returnIntent);
				finish();
			}
		});

		deleteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				SerializationTools.deletePoints(OpenDrawingActivity.this,imageFiles.get(currentPosition).getName());
				imageFiles.get(currentPosition).delete();
				imageFiles.remove(currentPosition);
				images.remove(currentPosition);
				
				if (imageFiles.size()==0){
					noDrawings();
				}else{
					coverFlow.releaseAllMemoryResources();
					coverFlow.reset();
					if (currentPosition>0)
						coverFlow.scrollToPosition(currentPosition-1);
					else
						coverFlow.scrollToPosition(0);
				}
			}
		});
		
		}else{
			noDrawings();
		}
	}
	
	
	ArrayList<File> imageFiles;
	ArrayList<Bitmap> images;
	
	BaseAdapter adapter = new BaseAdapter() {
		
		
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
	                v = new RoundedImageView(OpenDrawingActivity.this); 
	                viewHolder = new ViewHolder();
	                viewHolder.image = (RoundedImageView) v;
	                v.setTag(viewHolder);  
	           } else {  
	                viewHolder = (ViewHolder) v.getTag();  
	           }  
	           viewHolder.image.setBackgroundDrawable(OpenDrawingActivity.this.getResources().getDrawable(R.drawable.drawable_home_gallery_stroke));
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
			File dir = new File(getApplicationInfo().dataDir+"/Drawings/");
			
			if (dir.exists()){
				imageFiles = new ArrayList<File>();
				
				
				File[] files  = dir.listFiles();
				for (int i = 0; i < files.length; i++){
					imageFiles.add(files[i]);
				}
				
				getImages();
				return files.length;
			}
			else
				return 0;
		}
	};
	
	
	
	
	
	@Override 
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            finish();
	            return true; 
	        default: 
	            return super.onOptionsItemSelected(item);
	    } 
	} 
	
	
	public void noDrawings(){
		contentView.removeView(coverFlow);
		contentView.removeView(findViewById(R.id.open_drawing_bottom));
		TextView noDrawings = new TextView(this);
		noDrawings.setText(R.string.no_drawings);
		noDrawings.setGravity(Gravity.CENTER);
		noDrawings.setPadding(0, Tools.numtodp(this, 20), 0, 0);
		noDrawings.setTextSize(Tools.numtodp(this, 18));
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		contentView.addView(noDrawings,lp);
	}

	
	
	
	
	
}

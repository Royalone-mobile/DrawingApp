package com.songu.shadow.drawing.fragments;

import java.util.ArrayList;

import com.songu.shadow.drawing.R;
import com.songu.shadow.drawing.doc.Globals;
import com.songu.shadow.drawing.model.ColorName;
import com.songu.shadow.drawing.model.CustomColor;
import com.songu.shadow.drawing.tools.Tools;
import com.songu.shadow.drawing.view.ColorCaptureColorWheel;
import com.songu.shadow.drawing.view.ColorCaptureColorWheelSelection;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ColorCaptureFragment extends CustomFragment implements View.OnTouchListener{
	
	Context c;
	ColorCaptureColorWheel colorView;
	ColorCaptureColorWheelSelection colorViewSelection;
	Thread thread;
	
	TextView RGBText;
	TextView HEXText;
	TextView CMYKText;
	TextView colorName;
	
	Button saveButton;
	EditText colorTitle;
	int rgbColor;
	int blackColor = 0xffffff;
	double rgbRadian;
	double blackRadian = Math.PI;
	Bitmap map;
	int min;
	
	public static boolean running;

	public ColorCaptureFragment(LayoutInflater inflater, int resource) {
		super(inflater, resource);
		c = inflater.getContext();
		colorViewSelection = (ColorCaptureColorWheelSelection) fragmentLayout.findViewById(R.id.colorCaptureColorWheelSelection);
		colorView = (ColorCaptureColorWheel) fragmentLayout.findViewById(R.id.colorCaptureColorWheel);
		RGBText = (TextView) fragmentLayout.findViewById(R.id.color_cap_rgb);
		HEXText = (TextView) fragmentLayout.findViewById(R.id.color_cap_hex);
		CMYKText = (TextView) fragmentLayout.findViewById(R.id.color_cap_cmyk);
		colorName = (TextView) fragmentLayout.findViewById(R.id.colorName);
		
		colorTitle = (EditText) fragmentLayout.findViewById(R.id.color_cap_name);
		saveButton = (Button) fragmentLayout.findViewById(R.id.color_cap_save);
		colorView.setOnTouchListener(this);
		saveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (colorTitle.getText().toString().length()>0){
					ArrayList<CustomColor> colors = Tools.getSerializedColors(c);
					CustomColor col = colorView.getColor();
					col.location = Globals.g_cityName;
					col.name = colorTitle.getText().toString();
					//col.location = MainAppActivity.location;
					colors.add(col);
					
					Tools.serializeColors(colors,c);
					colorTitle.setText("");
					
					AlertDialog.Builder builder = new AlertDialog.Builder(c);
					builder.setMessage(col.name + "   Saved")
					       .setCancelable(false)
					       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					                //do things
					           }
					       });
					AlertDialog alert = builder.create();
					alert.show();
					
					
				}else{
					Tools.alert(R.string.please_enter_name, c);
				}
			}
		});
		
		colorView.setSelector(colorViewSelection);
		
		
		map = colorView.palette;
	}
	
	public void startColorChanger(){
		running = true;
		Log.d("SC", "startColorChanger");
		
		if (thread==null){
			thread = new Thread() {
		        public void run() { 
		            while (running) {
		            	try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
		            	if (running)
		            		colorView.setColor(Tools.getRandomColor(),false,false);
	                    ((Activity) c).runOnUiThread(new Runnable() {
	 
	                        @Override 
	                        public void run() {
	                        	if (running){
		                        	colorView.invalidate();
		                        	colorViewSelection.invalidate();		                        	
		                        	updateColorText();
	                        	}
	                        } 
	                    }); 
		                    

		            } 
		        } 
		    };
		    
		    //thread.start();
		}
	}
	
	public void updateColorText(){
		
		CustomColor currentColor = colorView.getColor();
		
		Tools.setColorTextData(currentColor, RGBText, HEXText, CMYKText);
        
        colorName.setText("#"+getColorNameFromRgb(currentColor.r, currentColor.g, currentColor.b));
	}
	
	public String getColorNameFromRgb(int r, int g, int b) {
        ArrayList<ColorName> colorList = ColorName.initColorList();
        ColorName closestMatch = null;
        int minMSE = Integer.MAX_VALUE;
        int mse;
        for (ColorName c : colorList) {
            mse = c.computeMSE(r, g, b);
            if (mse < minMSE) {
                minMSE = mse;
                closestMatch = c;
            }
        }

        if (closestMatch != null) {
            return closestMatch.getName();
        } else {
            return "No matched color name.";
        }
    }
	
	public void stopThread(){
		running = false;
		
		new Thread() {
	        public void run() {
	        	try {
	    			thread.join();
	    		} catch (InterruptedException e) {
	    			e.printStackTrace();
	    		}
	        } 
	    }.start();
		
		
		
	}
	
	@Override
	void exit(){
		Log.d("SC", "EXIT");
		colorView.exit();
		stopThread();
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if (v.equals(this.colorView))
		{
			if (event.getAction() == MotionEvent.ACTION_DOWN)
			{
				float x = event.getX();
				float y = event.getY();
				colorView.min = Math.min(colorView.width,colorView.height);
				min = Math.min(colorView.width,colorView.height);
				float xx = (colorView. width / 2 - (x));
				float yy = (colorView.height / 2 - (y ));
				double rad = Math.sqrt(xx * xx + yy * yy );
				if (rad < min / 2.3f && rad >= min / 3f )
				{					
					map = colorView.palette;
					int color = map.getPixel((int)(x ), (int)(y));
					this.rgbColor = color;
					double angle = Math.atan(yy/xx);
					
					
					//Math.toRadians(angdeg)
					double an = Math.toDegrees(angle);
					if (xx > 0 && yy < 0)
						an = 180 + an;
					else if (xx > 0 && yy > 0)
					{
						an = an - 180;
					}
					//colorView.setColor(new CustomColor(color), false,false);
					//colorView.setColor(new CustomColor(color), false, true);
					rgbRadian = Math.toRadians(an);
					
					CustomColor black = new CustomColor(this.blackColor);
					int newColor = mergeDarknessWithRGB(color,black.k);
					
					
					colorView.setColorFromTouch(new CustomColor(newColor), false,rgbRadian,blackRadian);
					//colorView.invalidate();
                	colorViewSelection.invalidate();		                        	
                	updateColorText();	
				}
				else if (rad < min / 3f && rad >= min / 3.45f)
				{
					//Toast.makeText(c, "Black Area", Toast.LENGTH_SHORT).show();
					if (map != null)
					{
						int color = map.getPixel((int)x, (int)y);		
						this.blackColor = color;
						//int k = Tools.getCMYK(color);
						double angle = Math.atan(yy/xx);
						double an = Math.toDegrees(angle);
						if (xx > 0 && yy < 0)
							an = 180 + an;
						else if (xx > 0 && yy > 0)
						{
							an = an - 180;
						}
						
						blackRadian = Math.toRadians(an);
						CustomColor black = new CustomColor(color);
						int newColor = mergeDarknessWithRGB(this.rgbColor,black.k);
						
						//String ss = Tools.getCMYK(color);
						//colorView.setColor(new CustomColor(color), false,true);
						colorView.setColorFromTouch(new CustomColor(newColor), false, rgbRadian, blackRadian);
						//colorView.invalidate();
	                	colorViewSelection.invalidate();		                        	
	                	updateColorText();	
					}
				}
			}
			if (event.getAction() == MotionEvent.ACTION_MOVE)
			{
				colorView.min = Math.min(colorView.width,colorView.height);
				min = Math.min(colorView.width,colorView.height);
				float x = event.getX();
				float y = event.getY();
				float xx = (colorView. width / 2 - (x));
				float yy = (colorView.height / 2 - (y ));
				double rad = Math.sqrt(xx * xx + yy * yy );
				if (rad < min / 2.3f && rad >= min / 3f )
				{					
					map = colorView.palette;
					int color = colorView.palette.getPixel((int)(x ), (int)(y));
					this.rgbColor = color;
					double angle = Math.atan(yy/xx);
					
					
					//Math.toRadians(angdeg)
					double an = Math.toDegrees(angle);
					if (xx > 0 && yy < 0)
						an = 180 + an;
					else if (xx > 0 && yy > 0)
					{
						an = an - 180;
					}
					//colorView.setColor(new CustomColor(color), false,false);
					//colorView.setColor(new CustomColor(color), false, true);
					rgbRadian = Math.toRadians(an);
					
					CustomColor black = new CustomColor(this.blackColor);
					CustomColor origin = new CustomColor(color);
					int newColor = mergeDarknessWithRGB(color,black.k);
					
					
					colorView.setColorFromTouch(new CustomColor(newColor), false,rgbRadian,blackRadian);
					//colorView.invalidate();
                	colorViewSelection.invalidate();		                        	
                	updateColorText();	
				}
				else if (rad < min / 3f && rad >= min / 3.45f)
				{
					//Toast.makeText(c, "Black Area", Toast.LENGTH_SHORT).show();
					int color = colorView.palette.getPixel((int)x, (int)y);		
					this.blackColor = color;
					//int k = Tools.getCMYK(color);
					double angle = Math.atan(yy/xx);
					double an = Math.toDegrees(angle);
					if (xx > 0 && yy < 0)
						an = 180 + an;
					else if (xx > 0 && yy > 0)
					{
						an = an - 180;
					}
					
					blackRadian = Math.toRadians(an);
					CustomColor black = new CustomColor(color);
					CustomColor origin = new CustomColor(this.rgbColor);
					int newColor = mergeDarknessWithRGB(this.rgbColor,black.k);
					
					//String ss = Tools.getCMYK(color);
					//colorView.setColor(new CustomColor(color), false,true);
					colorView.setColorFromTouch(new CustomColor(newColor), false, rgbRadian, blackRadian);
					//colorView.invalidate();
                	colorViewSelection.invalidate();		                        	
                	updateColorText();	
				}
			}
		}
		
		return true;
	}
	
	
	public static int mergeDarknessWithRGB(int c,int darkness){
		 float computedC = 0;
		 float computedM = 0;
		 float computedY = 0;
		 float computedK = 0;

		 int r = (c >> 16) & 0xFF;
		 int g = (c >> 8) & 0xFF;
		 int b = (c >> 0) & 0xFF;

		 // BLACK
		 if (r==0 && g==0 && b==0) {
		  computedK = 1;
		  return 0x000000;
		 }

		 computedC = 1 - (r/255f);
		 computedM = 1 - (g/255f);
		 computedY = 1 - (b/255f);
		 
		 float minCMY = Math.min(computedC,Math.min(computedM,computedY));

		 if (1 - minCMY != 0){
			 computedC = (computedC - minCMY) / (1 - minCMY) ;
			 computedM = (computedM - minCMY) / (1 - minCMY) ;
			 computedY = (computedY - minCMY) / (1 - minCMY) ;
		 }
		 
		 //Convert to RGB with dark
		 float r1 = 255 * (1 - computedC) * (1 - (float)darkness / 100);
		 float g1 = 255 * (1 - computedM) * (1 - (float)darkness / 100);
		 float b1 = 255 * (1 - computedY) * (1 - (float)darkness / 100);
		 
		 int color = (((int)r1 << 16) & 0xFF0000 )
				 	| (((int)g1 << 8) & 0x00FF00)
				 	| (((int)b1 << 0) & 0x0000FF);
		 
		 return color;
	}
	
	public void freeMemory()
	{
		if (map != null)
		{
			this.map.recycle();
			this.map = null;
		}
	}
}

package com.songu.shadow.drawing.view;

import com.songu.shadow.drawing.model.CustomColor;
import com.songu.shadow.drawing.tools.Tools;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ColorCaptureColorWheel extends View {
	
	public static final double colorRadiusMultiplyer = 2.6d;
	public static final double darknessRadiusMultiplyer = 3.2d;
	
	CustomColor color;
	ColorCaptureColorWheelSelection colorSelection;
	public Bitmap palette;


	public ColorCaptureColorWheel(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public ColorCaptureColorWheel(Context context, AttributeSet attrs) {
		super(context, attrs);
		p = new Paint();
		p.setAntiAlias(true);
		color = Tools.getRandomColor();
	}

	public ColorCaptureColorWheel(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public void setSelector(ColorCaptureColorWheelSelection s){
		colorSelection = s;
	}

	public void setColorFromTouch(CustomColor c, boolean invalidate,double angle1,double angle2)
	{
		color = c;
		
		setDrawingCacheEnabled(true);
		this.buildDrawingCache();
		//locateOnColorWheel(color, this.getDrawingCache(false),isDark);
		colorSelection.setData(angle1, angle2);
	
		if (invalidate){
			invalidate();
			colorSelection.invalidate();
		}
		setDrawingCacheEnabled(false); 
	}
	public void setColor(CustomColor c, boolean invalidate,boolean isDark){
		color = c;
		
		setDrawingCacheEnabled(true);
		this.buildDrawingCache();
		locateOnColorWheel(color, this.getDrawingCache(false),isDark);
	
		if (invalidate){
			invalidate();
			colorSelection.invalidate();
		}
		setDrawingCacheEnabled(false); 
	}
	
	public CustomColor getColor(){
		return color;
	}
	
	boolean exit;
	public void exit(){
		exit = true;
	}
	
	void locateOnColorWheel(CustomColor c, Bitmap b, boolean isDark){
		
		if (b!=null){
			double tempAngle;
			
			double bestAngle = 0;
			int closestOffset = 9999;
			
			int offset;
			CustomColor currentColor = new CustomColor(0);
			//if (!isDark)
			//{
				for (tempAngle = 0; tempAngle< (2*Math.PI); tempAngle +=.001){
					int x = (width/2)+(int)((min/colorRadiusMultiplyer)*Math.cos(tempAngle));
					int y = (height/2)+(int)((min/colorRadiusMultiplyer)*Math.sin(tempAngle));
					if (x<b.getWidth() && y<b.getHeight()){
						if (!exit){
							currentColor.setColorData(b.getPixel(x , y));
							offset = Tools.getColorDifference(c,currentColor);
							
							if(closestOffset>offset){
								closestOffset = offset;
								bestAngle = tempAngle;
							}
						}else
							break;
					}else{
						bestAngle = -1;
						break;
					}
					
				}
			//}
			
			
			
			
			
			
			double tempDarkAngle;
			
			double bestDarkAngle = 0;
			int closestDarkOffset = 9999;
			
			int offsetDark;
			CustomColor currentDarkColor = new CustomColor(0);
			//if (isDark)
			//{
				for (tempDarkAngle = 0; tempDarkAngle< (2*Math.PI); tempDarkAngle +=.001){
					int x = (width/2)+(int)((min/darknessRadiusMultiplyer)*Math.cos(tempDarkAngle));
					int y = (height/2)+(int)((min/darknessRadiusMultiplyer)*Math.sin(tempDarkAngle));
					if (x<b.getWidth() && y<b.getHeight()){
						if (!exit){
							currentDarkColor.setColorData(b.getPixel(x , y));
							offsetDark = Tools.getColorDifference(c,currentDarkColor);
							
							if(closestDarkOffset>offsetDark){
								closestDarkOffset = offsetDark;
								bestDarkAngle = tempDarkAngle;
							}
						}else
							break;
					}else{
						bestDarkAngle = -1;
						break;
					}
					
				}
			//}
			
			
			colorSelection.setData(bestAngle, bestDarkAngle);
		}
	}
	
	Paint p;
	
	public int width;
    public int height;
    public int min;
    
	
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		width = getWidth();
        height = getHeight();
        min = Math.min(width,height);
        if (this.palette == null)
        {
        	int width = getWidth();
        	int height =getHeight();
        	
        	this.palette = Bitmap.createBitmap(getWidth(),getHeight(),
                Bitmap.Config.ARGB_8888);
        //}
        //canvas.setBitmap(this.palette);
        
        Canvas can = new Canvas(this.palette);
        
        int colors[] = new int[13];
        float hsv[] = new float[3];
        hsv[1]=1;
        hsv[2]=1;
        for (int i=0; i<12; i++) {
            hsv[0] = (360 / 12) * i;
            colors[i] = Color.HSVToColor(hsv);
        } 
        colors[12] = colors[0];
        
        int outerOuterRingColors[] = new int [3];
        outerOuterRingColors[0]=Color.parseColor("#e3e3e3");
        outerOuterRingColors[1]=Color.parseColor("#dcdcdc");
        outerOuterRingColors[2]=Color.parseColor("#e3e3e3");

        int outerRingColors[] = new int [3];
        outerRingColors[0]=Color.parseColor("#d7d7d7");
        outerRingColors[1]=Color.parseColor("#d0d0d0");
        outerRingColors[2]=Color.parseColor("#d7d7d7");

        
        int innerRingColors[] = new int [3];
        innerRingColors[0]=Color.BLACK;
        innerRingColors[1]=Color.WHITE;
        innerRingColors[2]=Color.BLACK;
        
 
        SweepGradient sweepGradient = new SweepGradient(width/2, height/2, colors, null);
        SweepGradient outerOuterSweepGradient = new SweepGradient(width/2, height/2, outerOuterRingColors, null);
        SweepGradient outerSweepGradient = new SweepGradient(width/2, height/2, outerRingColors, null);
        
        SweepGradient innerSweepGradient = new SweepGradient(width/2, height/2, innerRingColors, null);
  
        p.setDither(true);
        
        p.setShader(outerOuterSweepGradient);
        can.drawCircle(width/2, height/2, min/2, p);
        
        
        p.setShader(outerSweepGradient);
        can.drawCircle(width/2, height/2, min/2.25f, p);
        
        p.setShader(sweepGradient); // rainbow
        can.drawCircle(width/2, height/2, min/2.3f, p);
        
        p.setShader(innerSweepGradient);
        can.drawCircle(width/2, height/2, min/3f, p);
        
        p.setShader(null);
        p.setColor(Color.WHITE);
        can.drawCircle(width/2, height/2, min/3.45f, p);
        }
        else
        {
        	Canvas can = new Canvas(this.palette);
        	p.setColor(color.color);
        	can.drawCircle(width/2, height/2, min/4.55f, p);
        }
        canvas.drawBitmap(this.palette, 0, 0,null);
	}

	
	
	

}

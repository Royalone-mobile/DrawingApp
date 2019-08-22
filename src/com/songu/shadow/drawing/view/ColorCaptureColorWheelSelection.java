package com.songu.shadow.drawing.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ColorCaptureColorWheelSelection extends View{
	
	double selectXDarkness, selectYDarkness;
	double selectX, selectY;
	int width, height , min;
	Paint p;
	double angle = -1;
	double darknessAngle = -1;

	public ColorCaptureColorWheelSelection(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public ColorCaptureColorWheelSelection(Context context, AttributeSet attrs) {
		super(context, attrs);
		p = new Paint();
		p.setAntiAlias(true);
	}

	public ColorCaptureColorWheelSelection(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public void setData(double a, double da){
		angle = a;
		darknessAngle = da;
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		width = getWidth();
        height = getHeight();
        min = Math.min(width,height);
		
		
        if (angle!=-1 && darknessAngle!=-1){
			selectX = (min/ColorCaptureColorWheel.colorRadiusMultiplyer)*Math.cos(angle);
	        selectY = (min/ColorCaptureColorWheel.colorRadiusMultiplyer)*Math.sin(angle);
	        
	        p.setDither(true);
	        p.setColor(Color.WHITE);
	        canvas.drawCircle( (int)((width/2) + selectX) , (int)((height/2) + selectY)  , min/60, p); //out circle
	         
	        selectXDarkness = (min/ColorCaptureColorWheel.darknessRadiusMultiplyer)*Math.cos(darknessAngle);
	        selectYDarkness = (min/ColorCaptureColorWheel.darknessRadiusMultiplyer)*Math.sin(darknessAngle);
	        
	        canvas.drawCircle( (int) ((width/2) + selectXDarkness) , (int) ((height/2) + selectYDarkness)  , min/60, p); //in circle
        }
	}
	
	

}
